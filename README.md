# Project Summary
Better codes, Better items, Better manage.
## Deploy
```shell
sh-3.14$ ./gradlew run distZip
sh-3.14$ cp build/distributions/daichang-*.zip .
sh-3.14$ unzip daichang-*.zip
sh-3.14$ daichang-*/bin/daichang-*.sh
```
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
 - [ ] Exposed SQL, data will be save into DBMS
 - [ ] Import, export to spreadsheet file
 - [ ] Advanced Filtering. Almost infinite Filter items
 - [ ] Full comments into source code. Implement JavaDoc (Dokka)
 - [ ] Unit tests

## ~~Phase 3~~
 - [ ] Works almost as an ERP system
 - [ ] More customization options like database connection
 - [ ] Implement Vue.js 