# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# 빌드
./gradlew build

# 전체 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.example.SomeTest"

# 단일 테스트 메서드 실행
./gradlew test --tests "com.example.SomeTest.methodName"

# 빌드 결과물 정리
./gradlew clean

# 클린 빌드
./gradlew clean build
```

Windows에서는 `./gradlew` 대신 `gradlew.bat` 또는 `.\gradlew` 사용.

## 프로젝트 구조

- **언어**: Java
- **빌드 도구**: Gradle (Kotlin DSL, `build.gradle.kts`)
- **테스트 프레임워크**: JUnit 5 (JUnit Jupiter, BOM 6.0.0)
- **소스 루트**: `src/main/java/`
- **테스트 루트**: `src/test/java/`

## 개발 노트

현재 소스 파일 없이 빌드 스캐폴딩만 구성된 초기 상태입니다. 구현 시 `src/main/java/` 아래에 패키지를 생성하고, 테스트는 동일한 패키지 경로로 `src/test/java/` 아래에 작성합니다.
