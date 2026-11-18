package com.nori.ai.gateway.integration.system;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(HttpSystemQueryAdapter.class)
public class SystemAdapterModuleConfiguration {}
