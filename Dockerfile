# ETAPA 1: Build da Aplicação
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos de gerenciamento do Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Baixa as dependências em cache
RUN ./mvnw dependency:go-offline

# Copia o código-fonte e gera o JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ETAPA 2: Imagem Final Leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]