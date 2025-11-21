FROM eclipse-temurin:17-jre AS builder
WORKDIR /builder
COPY src/main/docker/run_env.sh .
ARG JAR_FILE=target/*-bootexec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted

FROM eclipse-temurin:17-jre
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
COPY --chmod=555 --from=builder /builder/run_env.sh ./
#https://github.com/spring-projects/spring-boot/issues/37667
ENTRYPOINT ["./run_env.sh", "java","org.springframework.boot.loader.launch.JarLauncher"]
