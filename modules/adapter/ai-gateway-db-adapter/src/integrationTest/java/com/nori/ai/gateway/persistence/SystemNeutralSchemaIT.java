package com.nori.ai.gateway.persistence;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SystemNeutralSchemaIT extends PgIntegrationSupport {
    @Test void v7DataAndChecksumsSurviveV8AndNewSystemKeysRemainIsolated() throws Exception {
        try(var database=rawDatabase()) {
            database.bootstrap(); database.flywayConfiguration().target("7").load().migrate();
            var checksums=new java.util.LinkedHashMap<String,Integer>();
            try(var connection=database.connection(database.user());var sql=connection.createStatement()) {
                sql.execute("INSERT INTO gw.conversation VALUES ('legacy','TC','actor',now(),now(),now()+interval '30 days','ACTIVE')");
                sql.execute("INSERT INTO gw.audit_event(recorded_at,owner_system,actor_ref,request_id,event_type) VALUES(now(),'WM','actor','request','TEST')");
                try(var rows=sql.executeQuery("SELECT version,checksum FROM gw_flyway.flyway_schema_history WHERE success AND version IS NOT NULL")) {
                    while(rows.next()) checksums.put(rows.getString(1),rows.getInt(2));
                }
            }
            database.migrate();
            try(var connection=database.connection(database.user());var sql=connection.createStatement()) {
                try(var rows=sql.executeQuery("SELECT version,checksum FROM gw_flyway.flyway_schema_history WHERE success AND version IS NOT NULL AND version<>'8'")) {
                    int count=0; while(rows.next()){count++;assertThat(rows.getInt(2)).isEqualTo(checksums.get(rows.getString(1)));} assertThat(count).isEqualTo(checksums.size());
                }
                try(var rows=sql.executeQuery("SELECT count(*) FROM gw.conversation WHERE id='legacy'")){rows.next();assertThat(rows.getInt(1)).isOne();}
                try(var rows=sql.executeQuery("SELECT count(*) FROM information_schema.columns WHERE table_schema='gw' AND table_name='conversation' AND column_name='expires_at'")){rows.next();assertThat(rows.getInt(1)).isZero();}
                sql.execute("INSERT INTO gw.conversation VALUES ('new','New-7','actor',now(),now(),'ACTIVE')");
                assertThatThrownBy(()->sql.execute("INSERT INTO gw.conversation VALUES ('bad','_bad','actor',now(),now(),'ACTIVE')")).isInstanceOf(java.sql.SQLException.class);
                for(String system:new String[]{"New-7","Other"}) sql.execute("INSERT INTO kn.index_set VALUES ('"+system+"','00000000-0000-0000-0000-000000000001','model',3,repeat('a',64),'ACTIVE',now())");
                assertThatThrownBy(()->sql.execute("INSERT INTO kn.index_set VALUES ('New-7','00000000-0000-0000-0000-000000000002','model',3,repeat('a',64),'ACTIVE',now())")).isInstanceOf(java.sql.SQLException.class);
                String document="INSERT INTO kn.document VALUES ('New-7','00000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000001','bucket','key',repeat('b',64),'title','[\"READ\"]','[]')";
                sql.execute(document);
                assertThatThrownBy(()->sql.execute("INSERT INTO kn.parent_chunk VALUES ('Other','00000000-0000-0000-0000-000000000020','00000000-0000-0000-0000-000000000010','{}','header','masked',repeat('c',64))")).isInstanceOf(java.sql.SQLException.class);
                assertThatThrownBy(()->sql.execute(document.replace("000000000010","000000000011").replace("'key'","'key2'").replace("'[\"READ\"]'","'{}'"))).isInstanceOf(java.sql.SQLException.class);
                sql.execute("INSERT INTO kn.answer_snapshot VALUES ('New-7','00000000-0000-0000-0000-000000000030','historical-job','actor','masked','{}','[]',repeat('d',64),now())");
                sql.execute("DELETE FROM gw.conversation WHERE id='new'");
                try(var rows=sql.executeQuery("SELECT count(*) FROM kn.answer_snapshot")){rows.next();assertThat(rows.getInt(1)).isOne();}
                connection.setAutoCommit(false);
                sql.execute("INSERT INTO kn.glossary VALUES ('New-7','term','expansion',null,1)");
                assertThatThrownBy(()->sql.execute("INSERT INTO kn.question_case VALUES ('New-7','00000000-0000-0000-0000-000000000040','00000000-0000-0000-0000-000000000050',null,'{}',true,now())")).isInstanceOf(java.sql.SQLException.class);
                connection.rollback(); connection.setAutoCommit(true);
                try(var rows=sql.executeQuery("SELECT count(*) FROM kn.glossary")){rows.next();assertThat(rows.getInt(1)).isZero();}
            }
        }
    }
}
