# --- Stage 1: Build (Dùng Maven để tạo file .jar) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copy pom.xml trước để tải thư viện (tận dụng Docker Cache)
COPY pom.xml .
# Lệnh này giúp tải trước các dependencies, lần sau build sẽ nhanh hơn
RUN mvn -B -ntp -q -DskipTests dependency:go-offline

# 2. Copy source code và build
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# --- Stage 2: Runtime (Chỉ chứa môi trường chạy Java cho nhẹ) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo user 'spring' để bảo mật (không chạy bằng root)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy file .jar từ Stage 1 sang Stage 2
# Lưu ý: Lệnh này sẽ lấy file jar duy nhất trong target và đổi tên thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Cổng mặc định của Spring Boot
EXPOSE 8080

# Cấu hình biến môi trường Java (nếu cần tuning RAM)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Lệnh chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]