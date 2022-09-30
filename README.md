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

## Phase 2 (Current)
 - [x] Initialize Git (09.27.2022.)
 - [x] Exposed SQL, data will be save into DBMS (09.28.2022.)
 - [x] Import, export to spreadsheet file (09.30.2022.)
 - [ ] Import existing datas from ERP, auto-filling from them
 - [ ] Advanced Filtering. Almost infinite Filter items
 - [ ] Optimize source codes
 - [ ] Full comments into source code. Implement JavaDoc (Dokka)
 - [ ] Unit tests

## ~~Phase 3~~
 - [ ] Works almost as an ERP system
 - [ ] More customization options like database connection
 - [ ] Implement Vue.js 