loadAllItems();

//Methods
function loadAllItems() {
    $("#item-Tbody").empty();
    $.ajax({
        url: "http://localhost:8080/BackEnd_Web_exploded/item?option=GETALL",
        method: "GET",
        success: function (resp) {
            for (const item of resp.data) {
                let row = `<tr><td>${item.code}</td><td>${item.name}</td><td>${item.unitPrice}</td><td>${item.qtyOnHand}</td></tr>`;
                $("#item-Tbody").append(row);
            }
        },
        error: function (ob, state) {
            console.log(ob, state)
        }
    });

}


function saveItem() {
    var itemData = $("#itemForm").serialize();
    console.log(itemData);
    $.ajax({
        url: "http://localhost:8080/BackEnd_Web_exploded/item",
        method: "POST",
        data:itemData,
        success:function (res){
            if (res.status==200){
                alert(res.message);
                loadAllItems();
                // loadCustomerId();
            }else {
                alert(res.data);
            }
        },
        error: function (ob, textStatus, error) {
            console.log(ob);
            console.log(textStatus);
            console.log(error);
        }
    });
    $("#ItemstaticBackdrop").modal('hide');
    $("#save-Item").attr('disabled', true);
    $('#Item-id').attr('readonly', false);
}

function searchItemFromID(iCode){
    for (let i = 0; i < itemDB.length; i++) {
        if(itemDB[i].getItemCode()==iCode){
            return itemDB[i];
        }
    }
}


function clearAllItems() {
    $('#Item-id,#Item-name,#Item-price,#Item-quantity').val("");
    $('#Item-id,#Item-name,#Item-price,#Item-quantity').css('border', '2px solid #ced4da');
    $('#Item-id').focus();
    $("#save-Item").attr('disabled', true);
    loadAllCustomers();
    $("#lblItemCode,#lblItemName,#lblItemPrice,#lblItemQty").text("");
}





$("#save-Item").click(function () {
    saveItem();
    clearAllItems();
    loadAllItems();
    loadItemId();
});



$("#clear-Item").click(function (){
    clearAllItems();
    $("#save-Item").attr('disabled', true);
    // $("#update-customer").attr('disabled', true);
    $('#Item-id').attr('readonly', false);
});

$('#itemSearch_button').click(function (){
    var itemCode=$("#txtItem").val();
    $.ajax({
        url: "http://localhost:8080/BackEnd_Web_exploded/item?option=SEARCH&code="+itemCode,
        method: "GET",
        success: function (resp) {
            if (resp.status==200){
                $("#staticBackdrop2").modal('show');
                $("#Item-Code1").val(resp.code);
                $("#item-name1").val(resp.name);
                $("#item-price1").val(resp.unitPrice);
                $("#Item-Quantity1").val(resp.qtyOnHand);
                $('#txtItem').val("");
            }else{
                alert("Invalid Item Code.Try Again!");
            }
        },
        error: function (ob, state) {
            console.log(ob, state)
        }

    });
    $("#itemSearch_button").attr('disabled', true);

});

function clearAfterItemUpdate() {
    $('#Item-Code1,#item-name1,#item-price1,#Item-Quantity1').val("");
    $('#Item-Code1,#item-name1,#item-price1,#Item-Quantity1').css('border', '2px solid #ced4da');
    loadAllItems();
    $("#lblItemCode1,#lblItemName1,#lblItemPrice1,#lblItemQty1").text("");
}


$("#update-item1").click(function (){
    updateItem();
    clearAfterItemUpdate();
    // loadItemId();
});

$("#item_closebtn").click(function (){
    clearAfterItemUpdate();
});

function updateItem(){
    var itemOb={
        code:$("#Item-Code1").val(),
        name:$("#item-name1").val(),
        unitPrice:$("#item-price1").val(),
        qtyOnHand:$("#Item-Quantity1").val(),
    }

    $.ajax({
        url: "http://localhost:8080/BackEnd_Web_exploded/item",
        method: "PUT",
        contentType:"application/json",
        data: JSON.stringify(itemOb),
        success: function (res) {
            if (res.status == 200) {
                alert(res.message);
                loadAllItems();
            } else if (res.status == 400) {
                alert(res.message);
            } else {
                alert(res.data);
            }
        },
        error: function (ob, errorStatus) {
            console.log(ob);
        }
    });

    $("#staticBackdrop2").modal('hide');
}

$("#delete-item1").click(function (){
    deleteItem();
    loadAllItems();
    loadItemId();
});

function deleteItem(){
    var itemCode=$("#Item-Code1").val();
    $.ajax({
        url: "http://localhost:8080/BackEnd_Web_exploded/item?code=" +itemCode,
        method: "DELETE",
        success: function (res) {
            console.log(res);
            if (res.status == 200) {
                alert(res.message);
                loadAllItems();
                loadItemId();
            } else if (res.status == 400) {
                alert(res.data);
            } else {
                alert(res.data);
            }

        },
        error: function (ob, status, t) {
            console.log(ob);
            console.log(status);
            console.log(t);
        }
    });

    $("#staticBackdrop2").modal('hide');
}

$('#clear-item2').click(function (){
    $('#txtItem').val("");
    $('#txtItem').css('border', '2px solid #ced4da');
    $("#lblICode").text("");
    $("#itemSearch_button").attr('disabled', true);
});

// VALIDATIONS

const itemCodeRegEx =  /^(I00-)[0-9]{3,4}$/;
const itemNameRegEx = /^[A-z ]{3,20}$/;
const itemPriceRegEx = /^[0-9](.){1,6}$/;
const itemQtyRegEx = /^[0-9]{1,3}$/;

function itemformValid() {
    var itemID = $("#Item-id").val();
    $("#Item-id").css('border', '2px solid green');
    $("#lblItemCode").text("");
    if (itemCodeRegEx.test(itemID)) {
        var itemName = $("#Item-name").val();
        if (itemNameRegEx.test(itemName)) {
            $("#Item-name").css('border', '2px solid green');
            $("#lblItemName").text("");
            var itemPrice = $("#Item-price").val();
            if (itemPriceRegEx.test(itemPrice)) {
                var ItemQuantity = $("#Item-quantity").val();
                var resp = itemQtyRegEx.test(ItemQuantity);
                $("#Item-price").css('border', '2px solid green');
                $("#lblItemPrice").text("");
                if (resp) {
                    $("#Item-quantity").css('border', '2px solid green');
                    $("#lblItemQty").text("");
                    return true;
                } else {
                    $("#Item-quantity").css('border', '2px solid red');
                    $("#lblItemQty").text("Item Quantity is a required field : Minimum 7");
                    return false;
                }
            } else {
                $("#Item-price").css('border', '2px solid red');
                $("#lblItemPrice").text("Item Price is a required field : Pattern 100.00 or 100");
                return false;
            }
        } else {
            $("#Item-name").css('border', '2px solid red');
            $("#lblItemName").text("Item Name is a required field : Minimum 5, Max 20, Spaces Allowed");
            return false;
        }
    } else {
        $("#Item-id").css('border', '2px solid red');
        $("#lblItemCode").text("Item Code is a required field : Pattern I00-000");
        return false;
    }
}

function itemcheckIfValid() {
    var itemID = $("#Item-id").val();
    if (itemCodeRegEx.test(itemID)) {
        $("#Item-name").focus();
        var itemName = $("#Item-name").val();
        if (itemNameRegEx.test(itemName)) {
            $("#Item-price").focus();
            var itemPrice = $("#Item-price").val();
            if (itemPriceRegEx.test(itemPrice)) {
                $("#Item-quantity").focus();
                var itemQty = $("#Item-quantity").val();
                var resp = itemQtyRegEx.test(itemQty);
                if (resp) {
                    let res = confirm("Do you really need to add this Item..?");
                    if (res) {
                        saveItem();
                        clearAllItems();
                    }
                } else {
                    $("#Item-quantity").focus();
                }
            } else {
                $("#Item-price").focus();
            }
        } else {
            $("#Item-name").focus();
        }
    } else {
        $("#Item-id").focus();
    }
}

$('#Item-id,#Item-name,#Item-price,#Item-quantity,#txtItem,#Item-Code1,#item-name1,#item-price1,#Item-Quantity1').on('keydown', function (eventOb) {
    if (eventOb.key == "Tab") {
        eventOb.preventDefault(); // stop execution of the button
    }
});

$('#Item-id,#Item-name,#Item-price,#Item-quantity').on('blur', function () {
    itemformValid();
});

function setItemButton() {
    let b = itemformValid();
    if (b) {
        $("#save-Item").attr('disabled', false);
    } else {
        $("#save-Item").attr('disabled', true);
    }
}

$("#Item-id").on('keyup', function (eventOb) {
    setItemButton();
    if (eventOb.key == "Enter") {
        itemcheckIfValid();
    }
    if (eventOb.key == "Control") {
        var typedItemID = $("#Item-id").val();
        var srcItem = searchItemFromID(typedItemID);
        $("#Item-id").val(srcItem.getItemCode());
        $("#Item-name").val(srcItem.getItemName());
        $("#Item-price").val(srcItem.getItemprice());
        $("#Item-quantity").val(srcItem.getItemQuantity());
        $("#update-Item").attr('disabled', false);
        $("#save-Item").attr('disabled', true);
        $('#Item-id').attr('readonly', true);
    }
});

$("#Item-name").on('keyup', function (eventOb) {
    setItemButton();
    if (eventOb.key == "Enter") {
        itemcheckIfValid();
    }
});

$("#Item-price").on('keyup', function (eventOb) {
    setItemButton();
    if (eventOb.key == "Enter") {
        itemcheckIfValid();
    }
});

$("#Item-quantity").on('keyup', function (eventOb) {
    setItemButton();
    if (eventOb.key == "Enter") {
        itemcheckIfValid();
    }
});

$("#txtItem").on('keyup',function (eventOb){
    itemSearchButton();
});

function itemCheckformValid() {
    var itemCode = $("#txtItem").val();
    if (itemCodeRegEx.test(itemCode)) {
        $("#txtItem").css('border', '2px solid blue');
        $("#lblICode").text("");
        return true;
    } else {
        $("#txtItem").css('border', '2px solid red');
        $("#lblICode").text("Item Code is a required field : Pattern I00-000");
        return false;
    }
}


function itemSearchButton() {
    let b = itemCheckformValid();
    if (b) {
        $("#itemSearch_button").attr('disabled', false);
    } else {
        $("#itemSearch_button").attr('disabled', true);
    }
}
// ------
function checkFormValidate() {
    var itemID = $("#Item-Code1").val();
    $("#Item-Code1").css('border', '2px solid green');
    $("#lblItemCode1").text("");
    if (itemCodeRegEx.test(itemID)) {
        var itemName = $("#item-name1").val();
        if (itemNameRegEx.test(itemName)) {
            $("#item-name1").css('border', '2px solid green');
            $("#lblItemName1").text("");
            var itemPrice = $("#item-price1").val();
            if (itemPriceRegEx.test(itemPrice)) {
                var ItemQuantity = $("#Item-Quantity1").val();
                var resp = itemQtyRegEx.test(ItemQuantity);
                $("#item-price1").css('border', '2px solid green');
                $("#lblItemPrice1").text("");
                if (resp) {
                    $("#Item-Quantity1").css('border', '2px solid green');
                    $("#lblItemQty1").text("");
                    return true;
                } else {
                    $("#Item-Quantity1").css('border', '2px solid red');
                    $("#lblItemQty1").text("Item Quantity is a required field : Minimum 7");
                    return false;
                }
            } else {
                $("#item-price1").css('border', '2px solid red');
                $("#lblItemPrice1").text("Item Price is a required field : Pattern 100.00 or 100");
                return false;
            }
        } else {
            $("#item-name1").css('border', '2px solid red');
            $("#lblItemName1").text("Item Name is a required field : Minimum 5, Max 20, Spaces Allowed");
            return false;
        }
    } else {
        $("#Item-id").css('border', '2px solid red');
        $("#lblItemCode1").text("Item Code is a required field : Pattern I00-000");
        return false;
    }
}

function itemIfValid() {
    var ItemCode = $("#Item-Code1").val();
    if (itemCodeRegEx.test(ItemCode)) {
        $("#item-name1").focus();
        var itemName = $("#item-name1").val();
        if (itemNameRegEx.test(itemName)) {
            $("#item-price1").focus();
            var itemPrice = $("#item-price1").val();
            if (itemPriceRegEx.test(itemPrice)) {
                $("#Item-Quantity1").focus();
                var itemQty = $("#Item-Quantity1").val();
                var resp = itemQtyRegEx.test(itemQty);
                if (resp) {
                    let res = confirm("Do you really need to add this Customer..?");
                    if (res) {
                        saveItem();
                        clearAllItems();
                    }
                } else {
                    $("#Item-Quantity1").focus();
                }
            } else {
                $("#item-price1").focus();
            }
        } else {
            $("#item-name1").focus();
        }
    } else {
        $("#Item-Code1").focus();
    }
}

$('#Item-Code1,#item-name1,#item-price1,#Item-Quantity1').on('blur', function () {
    checkFormValidate();
});

function setItemUpdateButton() {
    let b = checkFormValidate();
    if (b) {
        $("#update-item1").attr('disabled', false);
    } else {
        $("#update-item1").attr('disabled', true);
    }
}

$("#Item-Code1").on('keyup', function (eventOb) {
    setItemUpdateButton();
    if (eventOb.key == "Enter") {
        itemIfValid();
    }

});

$("#item-name1").on('keyup', function (eventOb) {
    setItemUpdateButton();
    if (eventOb.key == "Enter") {
        itemIfValid();
    }
});

$("#item-price1").on('keyup', function (eventOb) {
    setItemUpdateButton();
    if (eventOb.key == "Enter") {
        itemIfValid();
    }
});

$("#Item-Quantity1").on('keyup', function (eventOb) {
    setItemUpdateButton();
    if (eventOb.key == "Enter") {
        itemIfValid();
    }
});



