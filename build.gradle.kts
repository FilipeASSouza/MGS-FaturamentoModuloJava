plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    jacoco
    java
}

group = "br.com.sankhya.mgs.ct"
version = "1.0.0"
val lughVersion = "5.+"
val skwVersion = "4.10b150"

repositories {
    mavenCentral()
    jcenter()
    maven("https://repository.jboss.org/nexus/content/groups/publisc-jboss")
    maven("https://repository.jboss.org/nexus/content/repositories")
    maven("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases")
    maven {
        url = uri("http://sankhyatec.mgcloud.net.br/api/v4/projects/173/packages/maven")
        name = "GitLab"
        metadataSources {
            artifact()
            mavenPom()
        }
        credentials(HttpHeaderCredentials::class.java) {
            name = "Private-Token"
            value = "YzDkSQZrWVnXYzG1RMQN"
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}
configurations.implementation.get().isCanBeResolved = true
dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("br.com.lughconsultoria", "lugh-lib", lughVersion)
    implementation("com.squareup.okhttp3", "logging-interceptor", "4.9.0")
    implementation("br.com.sankhya", "mge-modelcore", skwVersion)
    implementation("br.com.sankhya", "sanutil", skwVersion)
    implementation("br.com.sankhya", "jape", skwVersion)
    implementation("br.com.sankhya", "dwf", skwVersion)
    implementation("br.com.sankhya", "sanws", skwVersion)
    implementation("br.com.sankhya", "mge-param", skwVersion)
    implementation("br.com.sankhya", "skw-environment", skwVersion)
    implementation("br.com.sankhya", "cuckoo", skwVersion)
    implementation("br.com.sankhya", "print-service-base", skwVersion)
    implementation("jdom", "jdom", "1.0")
    implementation("org.beanshell", "bsh", "1.3.0")
    implementation("org.apache.directory.studio", "org.apache.commons.io", "2.1")
    implementation("org.apache.httpcomponents", "httpclient", "4.0.1")
    implementation("commons-fileupload", "commons-fileupload", "1.2")
    implementation("com.google.code.gson", "gson", "2.1")
    implementation("net.sf.jasperreports", "jasperreports", "4.0.0")
    implementation("net.sourceforge.jexcelapi", "jxl", "2.6.12")
    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.wildfly:wildfly-spec-api:16.0.0.Final")
    //testImplementation("junit", "junit", "4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks {
    kapt {
        useBuildCache = false
    }
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
            // xml.destination = file("${buildDir}/test-results")
        }
    }
    test {
        useJUnitPlatform()
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
    }
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
