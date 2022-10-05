const addModal = document.getElementById('addModal')
const filterModal = document.getElementById('filterModal')

$(document).ready(() => {
    /*
        Table
    */
    const table = $("table").DataTable({
        paging: false,
        dom: 'lrti' // No search area with table information bottom.
    });

    $("#searchBox").on("keyup", (event) => {
        const box = $(event.target)[0]; // search box
        const value = box.value;
        table.search(value).draw(); // trigger DataTable to search
    });

    $("#addButton").on('click', () => { // Click event: Add button
        Object.entries($("input")).forEach((v, i) => { // Get all input boxes.
            const input = v[1];
            if (input.id !== "inputIndex") { 
                input.value = ""; // Remove all values in input boxes except index for identification of which equipment is.
            }
        });
        $("#inputCabinetNumber").val($("#emptyNumber").text()); // Change number to empty number got from server.
        $("#inputImportDate").val(new Date().toISOString().slice(0, 10)); // Set import date to today in format 'yyyy-MM-dd'
        $("#inputOS").val("Win 10"); // Set Windows 10. Windows 10 is out 7 years from now. Why Legacy OSes still hanging around?
        $("#addModalDeleteButton").hide(); // Hide delete button used in when modal use as modification modal.
    });
    $("tr").on('click', event => { // If click row of table clicked.
        $("#addModalDeleteButton").show(); // Make visible delete button. 
        const selected = $(event.target)[0].parentElement.children; // Selected row
        const id = $(event.target)[0].parentElement.attributes["data-id"].value;
        $("#inputId").val(id);
        Array.prototype.forEach.call($("#addModal .modal-body form")[0], (input) => { // ForEach all of input in addModal
            if (input.id === "inputIndex") { // If inputIndex is selected, skip it. It is use for identification of what equipment is.
                return;
            }
            const inputItem = input.id.substring(5).toLowerCase(); // Each input has id value like inputCabinetNumber. Remove 'input' from id, make lower case. inputNumber -> number
            const values = [...selected].filter(v => { // Find value in table row. Each value in row has "data-info" attributes like 'number'. Find same id with inputItem above
                return v.attributes["data-info"].value.toLowerCase() === inputItem.toLowerCase();
            })
            let value = values[0].innerText; // Raw text of found input tag.
            if (input.id === "inputRAM" || input.id === "inputHDD") {
                value = value.slice(0, -2); // RAM, HDD value has 'GB' postfix. Remove that.
            }
            if (inputItem === "status") { // If selected input is status, handle in different way. (Simply, it is Select tag. Not input tag.)
                $("#inputStatus").val(value).change();
            } else {
                input.value = value;
            }
        });
    });
    $("#addModalSaveButton").on('click', () => { // If click save button, Submit form in Modal. 
        $("#addModalForm")[0].submit();
    });
    $("#deleteConfirmButton").on('click', () => { // If click confirm delete button, Change form url to /delete, and submit.
        $("#addModalForm").attr('action', '/delete');
        $("#addModalForm")[0].submit();
    });
    $("#filterModalApplyButton").on('click', () => { // Filter button clicked.
        $.ajax({
            type: 'post',
            url: '/filter',
            data: $('#filterModalForm').serialize()
        }).then((res,v,x) => { // res will be array of numbers.  [1, 2, 3]
            const result = JSON.parse(res); // Parse as JSON.
            $("#filterModalApplyButton").text("적용 (" + result.length + "개 발견)"); // Notify count of found equipments to button.
            Object.values($("tbody tr")).forEach(row => { // Looking every rows in table, if not included in filtered array, hide. if included, show
                const numberCol = row.children[0]; // number is first column. 
                const number = numberCol.innerText; 
                if (!result.includes(number * 1)) { // * 1 is required when convert string to number.
                    row.hidden = true;
                } else {
                    row.hidden = false;
                }
            });
        })
    })
    $("#filterModalReleaseButton").on('click', () => { // Release filter button clicked.
        Object.values($("tbody tr")).forEach(row => { // Make every row hidden is false.
            row.hidden = false;
        });
        $("#filterModalApplyButton").text("적용"); 
    });
    $("#addModalPrintButton").on('click', () => { // If click print button in addModal
        const index = $('#inputIndex').val()
        const number = $('#inputMgmtNumber').val()
        window.open('/print/'+ index + '/' + number) // Open new window to /print/index/number. Number 123 PC will be /print/1/EQ20210812333
    });
    /*
        Index 
    */
    $('.list-group a').click(function() { // Clicked in Print selection modal. Make active permanently.
      $(this).toggleClass('active')
    });
    // 3 Functions in below, send to printSelection function with desired equipment type
    $("#pcPrintSelectionButton").on('click', function() { 
        const id = $(this).attr("data-info")
        printSelection(id)
    });
    $("#laptopPrintSelectionButton").on('click', function() {
        const id = $(this).attr("data-info")
        printSelection(id)
    });
    $("#monitorPrintSelectionButton").on('click', function() {
        const id = $(this).attr("data-info")
        printSelection(id)
    });
    const printSelection = (id) => { // Process newly selected equipments to print.
        const listOfSelected = $("#"+id+"PrintModal .list-group .active") // Get list-item with active class.
        let list = []
        const rawPayload = JSON.parse($("#printPayload").val()) // Print form in Print card has hidden input tag for store raw json data to send post to the server.
        Array.prototype.forEach.call($(listOfSelected), v => {
            const number = v.attributes["data-info"] // Every list-item has data-info attribute to store mgmtNumber of equipment.
            if (number != null) {
                list.push(number.value) // push mgmtNumber to list.
            }
        });
        rawPayload[id] = list // if is PC, rawPayload["pc"] = [1, 2, 3]. In json, {"pc": [1, 2, 3]}
        $("#printPayload").val(JSON.stringify(rawPayload)) // Set hidden tag's value to json. 
        const printButton = $("#" + id + "PrintButton") // Get print button in card.
        if (printButton.text().match(/\d+/)) { // If button text has number, Already indicated number of old selections. Change number in that string. 
            printButton.text(printButton.text().replace(/\d/g, list.length))
        } else {
            printButton.text(printButton.text() + " (" + list.length + "개 선택됨)")
        }
    }
    $("#printButton").click(() => { // If click print button in Index.
        $("#printForm").submit(); // Submit form in print card with hidden json value tag.
    })
    $("#spreadsheetCardImportButton").click(() => { // If click import button
        $(".alert").remove(); // Remove all exists alerts
        $("#spreadsheetCardImportButton").attr('disabled', true); // Disable button
        $("#spreadsheetCardCleanButton").attr('disabled', true); // Disable export button too. Just in case.
        $("#spreadsheetCardImportButton").text('들여오는 중'); //
        const formData = new FormData()
        const file = $("#spreadsheetImportFile")[0].files[0] // File stores in array
        formData.append("file", file)
        $.ajax({
            type: "POST",
            url: $("#spreadsheetCardImportForm").attr("action"),
            processData: false,
            contentType: false,
            data: formData,
            success: (res) => {
                const response = JSON.parse(res) // {"data": MESSAGE_FROM_SERVER, success: it_is_successful?}
                let alertColor = "success"
                if (response.success == false) { // if received as failed.
                    alertColor = "danger"
                }
                $("#app").prepend("<div class=\"alert alert-" + alertColor + "\" role=\"alert\">" + response.data + "</div>") // Add alert for message.
                $("#spreadsheetCardImportButton").attr('disabled', false); // re-enable button
                $("#spreadsheetCardExportButton").attr('disabled', false); // re-enable button
                $("#spreadsheetCardImportButton").text('들여오기');
            }
        })
    })
    $("#inputMgmtNumber").on("keyup", function(key) {
        if (key.keyCode == 13) {
            $.ajax({
                type: "post",
                url: "/autofill",
                data: {
                    query: $(this).val()
                },
                success: (res) => {
                    const data = JSON.parse(res)
                    const index = data["index"]
                    if (index != "" || index != null) {
                        $("#inputModelName").val(data["modelName"])
                        $("#inputMfrDate").val(data["mfrDate"])
                        $("#inputSerial").val(data["serialNumber"])
                        $("#inputLastUser").val(data["lastUser"])
                    }
                    switch (index) {
                        case 1:
                        case 2:
                            $("#inputCPU").val(data["var1"]);
                            $("#inputRAM").val(data["var2"]);
                            $("#inputHDD").val(data["var3"]);
                            break;
                        case 3:
                            $("#inputInch").val(data["var2"]);
                            $("#inputCable").val(data["var1"]);
                            $("#inputResolution").val(data["var4"]);
                            $("#inputRatio").val(data["var5"]);
                            break;
                    }
                    //alert(JSON.stringify(data, null, 4))
                }
            })
        }
    })
})