package com.nori.ai.gateway.app.job;

import com.nori.ai.gateway.app.hooks.SeamRegistry;
import com.nori.ai.gateway.core.authz.*;
import com.nori.ai.gateway.core.port.*;
import com.nori.ai.gateway.core.retention.*;
import com.nori.ai.gateway.core.seam.*;
import com.nori.ai.gateway.integration.fixtures.StandardSystemStub;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;

class P11AppContextWiringIT extends P09QueueSupport {
    @Test void actualMockAndCorePoliciesUseOneGatewayClockAndOneRealSeamOfEachInterface() {
        assertThat(context.getBeansOfType(SystemQueryPort.class)).hasSize(1);assertThat(context.getBean(SystemQueryPort.class)).isExactlyInstanceOf(com.nori.ai.gateway.integration.system.HttpSystemQueryAdapter.class);
        for(Class<?> type:List.of(SourcePermissionMap.class,SourceAuthorizationStep.class,RetentionClassifier.class,TrimCompletenessEvaluator.class,DlqCompletenessEvaluator.class))assertThat(context.getBeansOfType(type)).hasSize(1);
        assertThat(context.getBeansOfType(PlanInjectionSeam.class)).hasSize(1);assertThat(context.getBeansOfType(DispatchInjectionSeam.class)).hasSize(1);
        assertThat(context.getBean(PlanInjectionSeam.class)).isSameAs(context.getBean(DispatchInjectionSeam.class)).isSameAs(context.getBean(SeamRegistry.class));
        assertThat(context.getBeansOfType(java.time.Clock.class)).hasSize(1);
        for(Object bean:List.of(context.getBean(StandardSystemStub.class),context.getBean(com.nori.ai.gateway.integration.system.HttpSystemQueryAdapter.class)))assertThat(ReflectionTestUtils.getField(bean,"clock")).isSameAs(clock());
        var call=new DelegatedCall("actor",registeredDlg("actor"));var registry=context.getBean(com.nori.ai.gateway.core.registry.CapabilityRegistry.class);var tool=registry.find(scope("actor"),"tc.eqp.getOperationalSnapshot").orElseThrow();
        assertThat(context.getBean(SourceAuthorizationStep.class).authorizeBefore(tool.sourceAllowlistRef(),scope("actor"),call)).isInstanceOf(SourceAuthorizationStep.Granted.class);
        var scope=scope("actor");var result=context.getBean(SystemQueryPort.class).query(new SystemQueryPort.Request(scope,tool,com.nori.ai.gateway.core.registry.SchemaContracts.parse("{\"eqp\":\"EQP-001\"}"),List.of(),call.dlg(),java.time.ZoneOffset.UTC,new com.nori.ai.gateway.core.job.CancellationToken()));assertThat(result.status()).isEqualTo(com.nori.ai.gateway.core.result.ResultStatus.SUCCESS);assertThat(context.getBean(StandardSystemStub.class).requests).hasSize(1);
    }
}
