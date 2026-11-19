package com.nori.ai.gateway.persistence.repository;

import com.nori.ai.gateway.core.result.AnswerSnapshot;
import com.nori.ai.gateway.core.registry.SchemaContracts;
import com.nori.ai.gateway.core.scope.VerifiedSystemScope;
import java.time.Clock;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

/** Job의 UK를 잠그는 종결 트랜잭션 안에서만 저장하며 대화 삭제 FK를 만들지 않습니다. */
public final class AnswerSnapshotRepository extends ScopedJdbc {
    public AnswerSnapshotRepository(JdbcClient jdbc,TransactionTemplate tx,Clock clock){super(jdbc,tx,clock);}
    public void save(VerifiedSystemScope scope,String jobId,AnswerSnapshot value) {
        transaction(()->{
            if(!scope.validAt(clock.instant())||!scope.requestId().equals(jobId))throw new IllegalArgumentException("스냅샷 인증 결속이 다릅니다");
            if(!scoped(scope,"SELECT EXISTS("+JOB+")").param("job",jobId).query(Boolean.class).single())throw new IllegalArgumentException("스냅샷 소유 결속이 다릅니다");
            scoped(scope,"""
                INSERT INTO kn.answer_snapshot(system_id,id,job_id,actor_ref,question_masked,answer,evidence,config_sha256,created_at)
                VALUES(:bound,:id,:job,:actor,:question,CAST(:answer AS jsonb),CAST(:evidence AS jsonb),:config,:now)
                ON CONFLICT(system_id,job_id) DO NOTHING
                """).param("id",UUID.randomUUID()).param("job",jobId).param("question",value.questionMasked()).param("answer",value.answer().toString())
                .param("evidence",value.evidence().toString()).param("config",value.configSha256()).update();
            assertSame(scope,jobId,value);return true;
        });
    }
    public void assertSame(VerifiedSystemScope scope,String jobId,AnswerSnapshot expected) {
        if(!scope.validAt(clock.instant())||!scope.requestId().equals(jobId))throw new IllegalArgumentException("스냅샷 인증 결속이 다릅니다");
        var stored=transaction(()->scoped(scope,"SELECT actor_ref,question_masked,answer::text,evidence::text,config_sha256 FROM kn.answer_snapshot WHERE system_id=:bound AND job_id=:job")
            .param("job",jobId).query((rs,n)->{
                if(!scope.actorRef().value().equals(rs.getString(1)))throw new IllegalStateException("스냅샷 소유자가 다릅니다");
                return new AnswerSnapshot(rs.getString(2),SchemaContracts.JSON.readTree(rs.getString(3)),SchemaContracts.JSON.readTree(rs.getString(4)),rs.getString(5));
            }).optional());
        if(stored.isEmpty()||!stored.get().contentSha256().equals(expected.contentSha256()))throw new IllegalStateException("스냅샷 본문 해시가 다릅니다");
    }
}
