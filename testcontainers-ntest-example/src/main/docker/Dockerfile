FROM eclipse-temurin:17-jre AS builder
WORKDIR /builder
ARG BUILD_ROOT=.
ARG JAR_FILE=${BUILD_ROOT}/target/*-bootexec.jar
ARG RESOURCE_DIR=${BUILD_ROOT}/src/main/docker
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted
COPY ${RESOURCE_DIR}/run_env.sh .

FROM eclipse-temurin:17-jre
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./
COPY --chmod=555 --from=builder /builder/run_env.sh ./
#https://github.com/spring-projects/spring-boot/issues/37667
ENTRYPOINT ["./run_env.sh", "java","org.springframework.boot.loader.launch.JarLauncher"]
