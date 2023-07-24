var productList = [];
var brandSel = document.getElementById("brandInput");
var categorySel = document.getElementById("categoryInput");
var role;
var brandData;
var originalProductData;
var modeType;
function getProductUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/products";
}

function getSuperVisorProductUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/supervisor/products";
}

function getBrandList() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  var url = baseUrl + "/api/brands";
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      dropdown(data);
    },
    error: handleAjaxError,
  });
}

function dropdown(data) {
  brandData = {};
  for (var i in data) {
    var brands = data[i];
    if (brandData.hasOwnProperty(brands.brand)) {
      brandData[brands.brand].push(brands.category);
    } else {
      brandData[brands.brand] = [brands.category];
    }
  }
  brandSel.length = 1;
  for (var brand in brandData) {
    brandSel.options[brandSel.options.length] = new Option(brand, brand);
  }
  brandSel.onchange = function () {
    categorySel.innerHTML = '<option value="">Select the Category</option>';
    var selected = this.value;
    categorySel.length = 1;
    for (var d = 0; d < brandData[selected].length; d++) {
      categorySel.options[categorySel.options.length] = new Option(
        brandData[selected][d],
        brandData[selected][d]
      );
    }
  };
}


function openModal(mode, id) {
  var $modal = $("#modal");
  var $modalForm = $("#modalForm");
  $modalForm.find(".invalid-feedback").hide();
  $("#modalSubmitButton").prop("disabled", true);
  // Reset the form and modal title based on the mode
  if (mode === "create") {
    modeType='create';
    $modalForm.trigger("reset");
    $("#productIdInput").val("");
    $("#barcodeInput").val("");
    $("#modalTitle").text("Create Product");
    $("#modalSubmitButton").text("Create");
    $modalForm.find(".form-control").removeClass("is-invalid");
    $modalForm.find(".form-control").removeClass("is-valid");
    $modalForm.find(".custom-select").removeClass("is-invalid");
    $modalForm.find(".custom-select").removeClass("is-valid");
  } else if (mode === "edit") {
    modeType='edit';
    $("#modalTitle").text("Edit Product");
    $("#modalSubmitButton").text("Update");
    $modalForm.find(".form-control").removeClass("is-invalid");
    $modalForm.find(".form-control").addClass("is-valid");
    $modalForm.find(".custom-select").removeClass("is-invalid");
    $modalForm.find(".custom-select").addClass("is-valid");
    $modalForm.find(".invalid-feedback").hide();
    displayEditProduct(id);
  }

  $modal.modal("show");
}

function checkProductDataChanged(){
var $modalForm = $('#modalForm');
    var currentDataJson = toJson($modalForm);
    currentDataJson=JSON.parse(currentDataJson);
    if (
      parseInt(currentDataJson.id) == originalProductData.id &&
      currentDataJson.barcode ==originalProductData.barcode &&
      currentDataJson.brandCategory==originalProductData.brandCategory &&
      currentDataJson.brandName == originalProductData.brandName &&
      currentDataJson.productName ==originalProductData.productName &&
      parseFloat(currentDataJson.mrp)== originalProductData.mrp
    ) {
      $("#modalSubmitButton").prop('disabled', true);
    } else {
      $("#modalSubmitButton").prop('disabled', false);
    }
}

function arrayToJson() {
  let json = [];
  for (i in productList) {
    let d = {};
    d["productName"] = JSON.parse(productList[i]).productName;
    d["brandName"] = JSON.parse(productList[i]).brandName;
    d["brandCategory"] = JSON.parse(productList[i]).brandCategory;
    d["mrp"] = JSON.parse(productList[i]).mrp;
    json.push(d);
  }
  return JSON.stringify(json);
}

// BUTTON ACTIONS
function addProduct(event) {
  // Set the values to update
  var $form = $("#modalForm");
  var brandName = brandSel.options[brandSel.selectedIndex].value;
   var brandCategory = categorySel.options[categorySel.selectedIndex].value;
  var json = toJson($form);
  var brandName = brandSel.value;
    var brandCategory = categorySel.value;
  productList.push(json);
  var url = getSuperVisorProductUrl();
  var jsonObj = arrayToJson();

  // Get the selected brand and category values


  $.ajax({
    url: url,
    type: "POST",
    data: jsonObj,
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      productList = [];
      successmsg("product added successfully");
      $("#modal").modal("hide");
      getProductList();
    },
    error: function (response) {
      if (response.status == 403) {
        errormsg("403 unauthorized");
      } else {
        var resp = JSON.parse(response.responseText);
        if (isJson(resp.message) == true) {
          var jsonObj = JSON.parse(resp.message);
          errormsg(jsonObj[0].message);
        } else {
          handleAjaxError(response);
        }
      }
      productList = [];
    },
  });

  return false;
}

function updateProduct(event) {
  // Get the ID
  var id = $("#modalForm input[name=id]").val();
  var url = getSuperVisorProductUrl() + "/" + id;

  // Set the values to update
  var $form = $("#modalForm");
  var json = toJson($form);

  // Get the selected brand and category values
  var brandName = brandSel.value;
  var brandCategory = categorySel.value;

  json["brandName"] = brandName;
  json["brandCategory"] = brandCategory;

  $.ajax({
    url: url,
    type: "PUT",
    data: json,
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      successmsg("product updated successfully");
      $("#modal").modal("hide");
      getProductList();
    },
    error: function(response){
          if(response.status == 403){
          errormsg("403 unauthorized");
          }
          else{
              var resp=JSON.parse(response.responseText);
              if(isJson(resp.message)==true){
                  var jsonObj=JSON.parse(resp.message);
                  errormsg(jsonObj[0].message);
              }
              else{
              handleAjaxError(response);}
          }
          }
  });

  return false;
}

function getProductList() {
table.clear().draw();
  table.row.add(["","","<i class='fa fa-refresh fa-spin'></i>","","",""]).draw();
  var url = getProductUrl();
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
      displayProductList(data);
    },
    error: handleAjaxError
  });
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function processData() {
  var file = $('#productFile')[0].files[0];
  if(file.name.split('.').pop()!="tsv"){
    errormsg("File is not a tsv file");
    return;
    }
    else{
      readFileData(file, readFileDataCallback);
    }
}

function readFileDataCallback(results) {
  fileData = results.data;
  var fileLen=fileData.length;
  if(fileLen==0){
      errormsg("Given file is empty");
      return;
  }
  else if(fileLen>5000){
        errormsg("Given file is greater than 5000");
      return;
  }
  else{
      uploadRows();
  }
}

function uploadRows() {
  updateUploadDialog();
    var json = JSON.stringify(fileData);
      $("#process-data").prop("disabled", true).find(".fa-refresh").show();
    var headers=["productName","brandName","brandCategory","mrp"];
    var jsond=JSON.parse(json);
    if(Object.keys(jsond[0]).length !=headers.length){
         errormsg("File column numbers do not match");
      return;
    }
    for(var i in headers){
     if(!jsond[0].hasOwnProperty(headers[i])){
        errormsg("File column do not match");
      return;
     }
    }
  var url = getSuperVisorProductUrl();

  // Make ajax call
  $.ajax({
    url: url,
    type: 'POST',
    data: json,
    headers: {
      'Content-Type': 'application/json'
    },
    success: function (response) {
      errorData=response;
      processCount=fileData.length;
      getProductList();
      successmsg("Product uploaded successfully")
    },
    error: function (response) {
      if(response.status ==403){
      errormsg("403 unauthorized");
      }
      else{
      var resp=JSON.parse(response.responseText);
      var jsonObj=JSON.parse(resp.message);
      errorData=jsonObj;
      errormsg("Error in uplaoding tsv file");
      $('#download-errors').prop('disabled',false);
      }
    },
    complete: function(){
        $("#process-data").prop("disabled", false).find(".fa-refresh").hide();
    }
  });
  $("#productFile").prop('disabled',true);
}

function downloadErrors() {
  writeFileData(errorData,'product-upload-errors');
}

// UI DISPLAY METHODS
function displayProductList(data) {
  var $tbody = $('#product_table').find('tbody');
  var dataRows=[];
  table.clear().draw();
  for (var i in data) {
    var serialNumber = parseInt(i) + 1;
    var e = data[i];
    var buttonHtml='';
    if(role=='supervisor'){
        buttonHtml+='<button class="btn btn-primary" onclick="openModal(\'edit\', ' + e.id + ')" title="edit"><i class="fa fa-pencil"></i></button>';
    }
    dataRows.push([
                        e.productName,
                        e.barcode,
                        e.brandName,
                        e.brandCategory,
                        e.mrp ? parseFloat(e.mrp).toFixed(2) : '.00',
                        buttonHtml
                      ]);
  }
  table.rows.add(dataRows).draw();
}

function displayEditProduct(id) {
  var url = getSuperVisorProductUrl() + "/" + id;
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
    originalProductData={
        id:data.id,
        barcode:data.barcode,
        brandCategory:data.brandCategory,
        brandName:data.brandName,
        mrp:data.mrp,
        productName:data.productName
    }
      displayProduct(data);
      $('#modal').modal('show');
    },
    error: handleAjaxError
  });
}

function resetUploadDialog() {
  // Reset file name
  var $file = $('#productFile');
  $file.val('');
  $('#productFileName').html("Choose File");
  // Reset various counts
  processCount = 0;
  fileData = [];
  errorData = [];
  // Update counts
  updateUploadDialog();
}

function updateUploadDialog() {
  $('#rowCount').html("" + fileData.length);
  $('#processCount').html("" + processCount);
  $('#errorCount').html("" + errorData.length);
}

function updateFileName() {
  var $file = $('#productFile');
  var fileName = $file.val().split('\\').pop();
  $('#productFileName').html(fileName);
}

function displayUploadData() {
  resetUploadDialog();
  $('#upload-product-modal').modal('show');
  $('#download-errors').prop('disabled',true);
  $('#process-data').prop('disabled',true);
  $('#productFile').prop('disabled',false);
}

function displayProduct(data) {
  $("#productIdInput").val(data.id);
  $("#brandInput").val(data.brandName);
  var selected = data.brandName;
  categorySel.length = 1;
  for (var d = 0; d < brandData[selected].length; d++) {
    categorySel.options[categorySel.options.length] = new Option(
      brandData[selected][d],
      brandData[selected][d]
    );
  }
  $("#brandInput").addClass('is-valid');
  $("#categoryInput").addClass('is-valid');
  $("#categoryInput").val(data.brandCategory);
  $("#productNameInput").val(data.productName);
  $("#barcodeInput").val(data.barcode);
  $("#mrpInput").val(data.mrp);
  $("#modalForm").off("submit").on("submit", function (event) {
    event.preventDefault();
    if ($("#productIdInput").val()) {
      updateProduct(event);
    } else {
      addProduct(event);
    }
  });
}

function resetModal() {
  $("#modalForm").trigger('reset');
  $("#productIdInput").val("");
  $("#modalTitle").text("");
  $("#modalSubmitButton").text("");
  $("#modalForm")
}
function activateUpload(){
    $("#process-data").prop('disabled',false);
}

var table;
// INITIALIZATION CODE
function init() {
    getBrandList();
    role=$("meta[name=role]").attr("content");
    var trgt=[1,4];
    if(role=="supervisor") trgt=[1,4,5];
    table = $('#product_table').DataTable({
        columnDefs: [
          {
            targets: trgt,
            orderable: false,
          },
          {
            targets: '_all', // Apply to all columns (use specific column index or column name here if needed)
            render: function (data, type, row, meta) {
              return '<div style="white-space: pre-wrap;">' + data + '</div>';
            },
          },
          {
            targets:4,
            className:'text-right',
          }
        ],
        order: [[0, 'asc']],
        drawCallback: function(settings) {
            var api = this.api();
            var pageInfo = api.page.info();
             if (pageInfo.pages <= 1) {
                 $('.dataTables_info').hide();
                 $('.dataTables_paginate').hide();
             } else {
                 $('.dataTables_info').show();
                 $('.dataTables_paginate').show();
             }
        },
        dom: 'Bfrtip',
        buttons: [],
        language: {
            search: '',
            searchPlaceholder: 'Search...'
        }
    });
    getProductList();
    // Customize the search input
        var searchInput = $('div.dataTables_filter input');
        // Add shadow effect on focus
        searchInput.on('focus', function() {
            $(this).addClass('shadow');
        });

        // Remove shadow effect on blur
        searchInput.on('blur', function() {
            $(this).removeClass('shadow');
        });

  // Add event listener for submit button in modal
  $('#modalSubmitButton').click(function (event) {
    event.preventDefault();
    if ($("#productIdInput").val()) {
      updateProduct(event);
    } else {
      addProduct(event);
    }
  });

  // Add event listener for cancel button in modal
  $('#modal').on('hidden.modal', function () {
    resetModal();
  });
  $('#refresh-data').click(getProductList);
  $('#process-data').click(processData);
  $('#download-errors').click(downloadErrors);
  $('#productFile').on('change', updateFileName);
    $('#upload-data').click(displayUploadData);
    $('#productFile').click(activateUpload);

  // Add event listener for upload button
  $('#upload-data').click(function () {
    $('#upload-product-modal').modal('show');
  });
}

function validateForm() {
    var brand=$("#brandInput").val().trim();
    var category=$("#categoryInput").val().trim();
    var productName=$("#productNameInput").val().trim();
    var mrp = $("#mrpInput").val();
  var isValid = true;

  if(productName.length >30 || productName.length===0){
          $("#productNameInput").addClass('is-invalid');
        $("#productNameInput").siblings('.invalid-feedback').text('Product Name cannot be empty and should be less than 30 characters.').show();
        $("#modalSubmitButton").prop('disabled', true);
        return false;
    }
    else{
          $("#productNameInput").addClass('is-valid');
    }

  if(brand.length >30 || brand.length===0){
        $("#brandInput").addClass('is-invalid');
        $("#brandInput").siblings('.invalid-feedback').text('Please select brand').show();
        $("#modalSubmitButton").prop('disabled', true);
        return false;
    }
    else{
      $("#brandInput").addClass('is-valid');
    }
    if(category.length >30 || category.length===0){
          $("#categoryInput").addClass('is-invalid');
          $("#categoryInput").siblings('.invalid-feedback').text('Please select category').show();
          $("#modalSubmitButton").prop('disabled', true);
          return false;
      }
      else{
          $("#categoryInput").addClass('is-valid');
      }

  if (isNaN(mrp) || mrp <= 0 || mrp> 100000000) {
        $("#mrpInput").addClass('is-invalid');
        $("#mrpInput").siblings('.invalid-feedback').text('MRP should be greater than 0 and less than 100000000').show();
        $("#modalSubmitButton").prop('disabled', true);
        return false;
  }
  else{
    $("#mrpInput").addClass('is-valid');
  }



  $("#modalSubmitButton").prop('disabled', false);
  return true;
}



$(document).ready(function (){
    $(document).on('input change', '#modal .modal-body input , #brandInput, #categoryInput', function() {

      var $input = $(this);
      if ($input.val().trim() !== '') {
        $input.removeClass('is-invalid');
        $input.siblings('.invalid-feedback').hide();
        $input.addClass('is-valid');
      } else {
        $input.addClass('is-invalid');
        $input.siblings('.invalid-feedback').show();
      }
      validateForm();
      if(modeType==='edit')checkProductDataChanged();
    });

  init();

});

$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('products-nav');
    navItem.classList.add('active');
});
