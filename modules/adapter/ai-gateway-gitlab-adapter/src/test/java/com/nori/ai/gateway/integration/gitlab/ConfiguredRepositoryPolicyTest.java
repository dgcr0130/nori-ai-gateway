package com.nori.ai.gateway.integration.gitlab;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class ConfiguredRepositoryPolicyTest {
 @Test void repositoryPathsCannotEscapeAndEmptyPrefixIsRoot(){var repo=new GitLabSourcePolicy.Repository("group/project","group/project",Set.of("main"),Set.of("src"),"CONFIG");assertThat(GitLabSourcePolicy.pathAllowed(repo,"src/a.java")).isTrue();for(String path:List.of("../src/a","src/../secret","/src/a","src%2Fsecret","other/a"))assertThat(GitLabSourcePolicy.pathAllowed(repo,path)).isFalse();var root=new GitLabSourcePolicy.Repository("1","repo",Set.of("main"),Set.of(""),"CONFIG");assertThat(GitLabSourcePolicy.pathAllowed(root,"a.java")).isTrue();assertThat(GitLabSourcePolicy.pathAllowed(root,"../secret")).isFalse();}
}
