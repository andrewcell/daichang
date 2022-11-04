# Project Summary
Better codes, Better items, Better manage.
## Deploy
```shell
sh-3.14$ ./gradlew run distZip
sh-3.14$ cp build/distributions/daichang-*.zip .
sh-3.14$ unzip daichang-*.zip
sh-3.14$ daichang-*/bin/daichang-*.sh
```
## Troubleshooting
In Linux error might occur if fontconfig library is not installed when exporting to spreadsheet file.
```shell
[user@localhost ~]$ dnf install fontconfig
```
Install fontconfig or libfontconfig to prevent errors.
## Environment Variables
Daichang uses environment variable to configure server.
 - ```daichang_port``` Listening port
 - ```daichang_host``` Listening host
 - ```db_url``` Database connect url
 - ```db_user```Database User name
 - ```db_pass```Database User password
## Phase 1 (Branched)
 - [x] Spreadsheet as database
 - [x] Add, Modify, Remove the entry
 - [x] Simple search box (09.21.2022.)
 - [x] Table filter (09.22.2022.)
 - [x] Print as a cabinet label (09.26.2022.)
 - [x] Spreadsheet file caching (09.27.2022.)
 - [x] Better Javascript codes (09.27.2022.)

## Phase 2 (Branched)
 - [x] Initialize Git (09.27.2022.)
 - [x] Exposed SQL, data will be save into DBMS (09.28.2022.)
 - [x] Import, export to spreadsheet file (09.30.2022.)
 - [x] Import existing data from ERP, autofill form from them (10.05.2022.)
 - [x] ~~Advanced Filtering. Almost infinite Filter items~~ (Canceled)
 - [x] Optimize source codes (11.01.2022.)
 - [x] Full comments into source code. Implement JavaDoc (Dokka) (11.04.2022.)
 - [x] Unit tests (Do continuously)

## Phase 3 (Current)
 - [ ] Implement Vue.js
 - [ ] More customization options like database connection

## ~~Phase 4~~
 - [ ] Works almost as an ERP system