# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Windows 환경에서는 `JAVA_HOME`을 먼저 설정해야 한다.

```powershell
$env:JAVA_HOME = "C:\Users\User\.jdks\temurin-17.0.19"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

```powershell
# 빌드
.\gradlew build

# 전체 테스트 실행
.\gradlew test

# 단일 테스트 클래스 실행
.\gradlew test --tests "org.example.store.DataStoreTest"

# 단일 테스트 메서드 실행
.\gradlew test --tests "org.example.store.DataStoreTest.put_항목을_저장하고_조회한다"

# 애플리케이션 실행 (메인 클래스 직접 실행)
.\gradlew run   # run task 미설정 시 아래 방법 사용
.\gradlew build && java -cp build\classes\java\main org.example.DataMonitor

# 클린 빌드
.\gradlew clean build
```

## 아키텍처

```
org.example
├── DataMonitor.java          # 진입점 — store / simulator / monitor 조립 후 ShutdownHook 등록
├── model/
│   └── DataEntry.java        # 데이터 항목 모델 (id, key, value, Status, updatedAt, updateCount)
├── store/
│   └── DataStore.java        # ConcurrentHashMap 기반 스레드 안전 인메모리 저장소
├── monitor/
│   └── ConsoleMonitor.java   # ScheduledExecutor로 500ms마다 ANSI 코드를 사용해 콘솔 전체 갱신
└── simulator/
    └── DataSimulator.java    # PoC용 가짜 데이터 생성기 — 실제 시스템에서 외부 데이터 소스로 교체
```

**데이터 흐름**: `DataSimulator` → `DataStore` (put/update/remove) → `ConsoleMonitor` (read-only polling)

- `DataStore`는 읽기/쓰기 모두 허용하는 공유 상태. `ConcurrentHashMap`으로 원자성 보장.
- `ConsoleMonitor`는 `DataStore`에서 스냅샷을 읽어 렌더링만 담당 (부수효과 없음).
- `DataSimulator`는 `DataStore`에만 쓰기 수행. 실제 배포 시 이 클래스만 교체하면 된다.

## 환경 설정

- **JDK**: Eclipse Temurin 17 (`C:\Users\User\.jdks\temurin-17.0.19`)
- **인코딩**: `build.gradle.kts`에 `options.encoding = "UTF-8"` 설정됨 (Windows CP949 기본값 회피)
- **ANSI 컬러**: Windows Terminal / IntelliJ 콘솔에서 정상 동작. CMD에서는 색상이 깨질 수 있음.
