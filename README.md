# DataMonitor

콘솔 기반 실시간 데이터 상태 모니터링 관리자 도구 (PoC)

## 실행 방법

### 1. Java 환경 설정 (최초 1회)

```powershell
$env:JAVA_HOME = "C:\Users\User\.jdks\temurin-17.0.19"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### 2. 빌드

```powershell
.\gradlew build
```

### 3. 실행

```powershell
java "-Dfile.encoding=UTF-8" -cp build\classes\java\main org.example.DataMonitor
```

종료: `Ctrl+C`

> 한글과 색상이 올바르게 표시되려면 **Windows Terminal** 사용을 권장합니다.

---

## 화면 구성

```
╔══════════════════════════════════════════════════════════════╗
║            DATA MONITOR  —  Admin Console (PoC)             ║
╚══════════════════════════════════════════════════════════════╝
  갱신: 10:13:54.127   가동: 3s   누적 업데이트: 21

  [ 상태 요약 ]
  전체: 13   ACTIVE(11)  STALE(2)  ERROR(0)

  ID         KEY                       VALUE           STATUS   UPDATED
  ──────────────────────────────────────────────────────────────
  26bac670   network.in[11]            OK              STALE    10:13:56.524
  8f915815   queue.depth[7]            13.1%           ACTIVE   10:13:56.223
  7c8e57aa   cache.hit_rate[new]       8.7%            ACTIVE   10:13:56.128

  [Ctrl+C] 종료
```

- 500ms마다 화면 전체 갱신
- 데이터 항목을 최근 업데이트 순으로 표시
- 상태별 색상 구분: `ACTIVE`(녹색) / `STALE`(노란색) / `ERROR`(빨간색)

---

## 아키텍처

```
DataMonitor (main)
├── DataStore          — ConcurrentHashMap 기반 스레드 안전 인메모리 저장소
├── DataSimulator      — PoC용 가짜 데이터 생성기 (실제 연동 시 교체)
└── ConsoleMonitor     — ANSI 코드로 콘솔을 주기적으로 갱신
```

실제 시스템 연동 시 `DataSimulator`를 외부 데이터 소스(DB, API 등)로 교체하면 됩니다.

---

## 테스트 실행

```powershell
# 전체 테스트
.\gradlew test

# 단일 테스트 클래스
.\gradlew test --tests "org.example.store.DataStoreTest"
```

## 환경

- Java 17 (Eclipse Temurin)
- Gradle 9.x (Kotlin DSL)
- JUnit 5
