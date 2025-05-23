# conbot-server

콘봇의 이미지 데이터베이스 역할을 담당해주는 서버입니다.

## features

* 콘봇의 이미지 데이터베이스 담당
* 저장된 이미지 링크화(GET)
* 암호를 통한 수정 접근 보호
* 쉽게 콘봇 서버와 상호작용이 가능한 페이지 제공

## Building & Running

프로젝트를 빌드하기 위해선 gradle 로 명령을 요쳥해야합니다. 자세한건 아랫 표를 참고해주세요.

| Task            | Description                                                             |
|-----------------|-------------------------------------------------------------------------|
| `./gradlew test` | test 에 있는 내용을 실행합니다.                                                    |
| `./gradlew build` | 소스를 모두 빌드시킵니다.                                                          |
| `./gradlew buildFatJar` | 모든 의존성과 소스를 빌드시켜 FatJar 파일로 만듭니다.<br/> **프로덕션 파일**을 생성할때 사용합니다.         |
| `./gradlew buildWeb` | webui 소스를 빌드후 콘봇 서버에 적용시킵니다.<br/>이 작업은 콘봇-서버를 풀스택으로 테스트하거나 빌드할 때 사용합니다. |
| `./gradlew runBuildPage`    | webui 소스를 빌드합니다.                                                        |
| `run`           | 서버를 실행시킵니다 (권장하지 않음)                                                    |

### How to Run
>⚠️ 콘봇 서버는 jar로 빌드후 프로덕션용으로 구동하는 걸 적극 권장합니다! `./gradlew buildFatJar`을 통하여 소스를 빌드 후 실행해주세요!

### ✅ 1. 프로젝트 빌드 (FatJar)
전체 의존성과 애플리케이션 소스를 포함한 **실행 가능한 FatJar**를 생성합니다:

```bash
./gradlew buildFatJar
```
### ✅ 2. 빌드 파일 실행
프로젝트 폴더에 있는 **FatJar 파일**을 사용해 서버를 실행합니다
```bash
java -jar build/libs/conbot-server.jar
```
---
### ✅ 즉석으로 테스트해보기
> 🪲 디버깅시 자세한 로깅을 확인하기 위해 setting.properties 파일이 생성된다면 **status** 옵션을 **development**로 변경하시는걸 권장드립니다!

디버깅을 위해 IDE에서 즉석으로 테스트해볼수 있습니다! 먼저 WebUI 최신 내용과 통합을 위하여 WebUI를 빌드후 콘봇-서버에 **통합**시켜주세요.
```
./gradlew buildWeb
```
**ApplicationKt** 파일에 있는 **main 함수**를 시작하면 서버가 시작합니다!

---
아랫 내용이 뜬다면 서버 구동에 성공한겁니다!

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## WebUI
콘봇-서버 기본 WebUI는 별도의 레포지토리에서 관리 됩니다! 자세한 내용은 conbot-server-webui 레포지토리에서 참고해주세요.
<br/>
### [링크](https://github.com/ZeroBall526/conbot-server-webui)

