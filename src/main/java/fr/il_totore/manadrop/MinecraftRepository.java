package fr.il_totore.manadrop;

import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import java.net.URI;

public class MinecraftRepository {

    private static StandardMinecraftRepository instance;

    public static StandardMinecraftRepository getInstance() {
        return instance;
    }

    public static void setInstance(StandardMinecraftRepository instance) {
        MinecraftRepository.instance = instance;
    }


    public static MavenArtifactRepository spigotRepository() {
        return instance.spigotRepository();
    }

    public static MavenArtifactRepository sonatypeRepository() {
        return instance.sonatypeRepository();
    }

    public static MavenArtifactRepository paperRepository() {
        return instance.paperRepository();
    }

    public static class StandardMinecraftRepository {

        private RepositoryHandler repositoryHandler;

        public StandardMinecraftRepository(RepositoryHandler repositoryHandler) {
            this.repositoryHandler = repositoryHandler;
        }

        public RepositoryHandler getRepositoryHandler() {
            return repositoryHandler;
        }

        public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
            this.repositoryHandler = repositoryHandler;
        }

        public MavenArtifactRepository spigotRepository() {
            return repositoryHandler.maven(repository ->
                    repository.setUrl(URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")));
        }

        public MavenArtifactRepository sonatypeRepository() {
            return repositoryHandler.maven(repository ->
                    repository.setUrl(URI.create("https://oss.sonatype.org/content/repositories/snapshots")));
        }

        public MavenArtifactRepository paperRepository() {
            return repositoryHandler.maven(repository ->
                    repository.setUrl(URI.create("https://papermc.io/repo/repository/maven-public/")));
        }
    }
}