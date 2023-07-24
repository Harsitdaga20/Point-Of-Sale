var brandList=[];
var role;
var originalBrandData;
var modeType;
function getBrandUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/brands";
}

function getSupervisorBrandUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/supervisor/brands";
}

function openModal(mode, id) {
  var $modal = $('#modal');
  var $modalForm = $('#modalForm');
  $modalForm.find('.invalid-feedback').hide();
  $("#modalSubmitButton").prop('disabled', true);
  // Reset the form and modal title based on the mode
  if (mode === 'create') {
  modeType='create';
    $modalForm.trigger('reset');
    $("#brandIdInput").val("");
    $("#modalTitle").text("Create Brand");
    $("#modalSubmitButton").text("Create");
    $modalForm.find('.form-control').removeClass('is-valid');
    $modalForm.find('.form-control').removeClass('is-invalid');
  } else if (mode === 'edit') {
  modeType='edit'
    $("#modalTitle").text("Edit Brand");
    $("#modalSubmitButton").text("Update");
    $modalForm.find('.form-control').removeClass('is-invalid');
    $modalForm.find('.form-control').addClass('is-valid');
    $modalForm.find('.invalid-feedback').hide();
    displayEditBrand(id);
  }

  $modal.modal('show');
}

function checkBrandDataChanged() {
  var $modalForm = $('#modalForm');
    var currentDataJson = toJson($modalForm);
    currentDataJson=JSON.parse(currentDataJson);
    if (
      parseInt(currentDataJson.id) == originalBrandData.id &&
      currentDataJson.brand == originalBrandData.brand &&
      currentDataJson.category == originalBrandData.category
    ) {
      $("#modalSubmitButton").prop('disabled', true);
    } else {
      $("#modalSubmitButton").prop('disabled', false);
    }
}

function arrayToJson(){
    let json=[];
    for(i in brandList){
    let d={};
    d["brand"]=JSON.parse(brandList[i]).brand;
    d["category"]=JSON.parse(brandList[i]).category;
    json.push(d);
    }
    return JSON.stringify(json);
}

// BUTTON ACTIONS
function addBrand(event) {
  // Set the values to update
  var $form = $("#modalForm");
  var json = toJson($form);
  brandList.push(json);
  var url = getSupervisorBrandUrl();
    var jsonObj=arrayToJson();
  $.ajax({
    url: url,
    type: 'POST',
    data: jsonObj,
    headers: {
      'Content-Type': 'application/json'
    },
    success: function (response) {
        brandList=[];
        successmsg("Brand Added Successfully");
      $('#modal').modal('hide');

      getBrandList();

    },
    error:function(response){
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
        brandList=[];
    }
  });

  return false;
}


function updateBrand(event) {
  // Get the ID
  var id = $("#modalForm input[name=id]").val();
  var url = getSupervisorBrandUrl() + "/" + id;

  // Set the values to update
  var $form = $("#modalForm");
  var json = toJson($form);
  brandList.push(json);
  var jsonObj=arrayToJson(brandList);

  $.ajax({
    url: url,
    type: 'PUT',
    data: json,
    headers: {
      'Content-Type': 'application/json'
    },
    success: function (response) {
      $('#modal').modal('hide');
      successmsg("Brand Updated Successfully");
      getBrandList();
    },
    error: function(response){
           if(response.status == 403){
           errormsg("403 unauthorized");
           }
           else{
               var resp=JSON.parse(response.responseText);
               if(isJson(resp.message)==true){
                   var jsonObj=JSON.parse(resp.message);
                   errormsg(jsonObj[0].message)
               }
               else{
               handleAjaxError(response);}
           }
           }
  });

  return false;
}

function getBrandList() {
    table.clear().draw();
    table.row.add(["","<i class='fa fa-refresh fa-spin'></i>",""]).draw();
  var url = getBrandUrl();
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
      displayBrandList(data);
    },
    error: handleAjaxError
  });
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

function processData() {
  var file = $('#brandFile')[0].files[0];
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
  // Update progress
  updateUploadDialog();
  var json = JSON.stringify(fileData);
  $("#process-data").prop("disabled", true).find(".fa-refresh").show();
  var headers=["brand","category"];
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
  var url = getSupervisorBrandUrl();
  // Make ajax call
  $.ajax({
    url: url,
    type: 'POST',
    data: json,
    headers: {
      'Content-Type': 'application/json'
    },
    success: function (response) {
    successmsg("Brand Uploaded Successfully");
    errorData=response;
    processCount=fileData.length;
    getBrandList();
    },
    error: function (response) {
      if(response.status ==403){
      errormsg('403 unauthorized')
      }
      else{
      var resp=JSON.parse(response.responseText);
      var jsonObj=JSON.parse(resp.message);
      errorData=jsonObj;
      errormsg("Error in uploading tsv file");
      $('#download-errors').prop('disabled',false);
      }
    },
    complete: function(){
        $("#process-data").prop("disabled", false).find(".fa-refresh").hide();
    }
  });
  $("#brandFile").prop('disabled',true);
}

function downloadErrors() {
  writeFileData(errorData,'brand-upload-errors');
}

// UI DISPLAY METHODS

function displayBrandList(data) {
  var dataRows=[];
  var $tbody = $('#brand_table').find('tbody');

  table.clear().draw();
  for (var i in data) {

    var serialNumber = parseInt(i) + 1;
    var e = data[i];
    var buttonHtml='';
    if(role=='supervisor'){
        buttonHtml+= '<button class="btn btn-primary" onclick="openModal(\'edit\', ' + e.id + ')" title="edit"><i class="fa fa-pencil"></i> </button>';
    }
    dataRows.push([e.brand,e.category,buttonHtml]);
  }
  table.rows.add(dataRows).draw();
}

function displayEditBrand(id) {
  var url = getSupervisorBrandUrl() + "/" + id;
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
    originalBrandData = {
            id:data.id,
            brand: data.brand,
            category: data.category
          };
    var $modalForm = $('#modalForm');
      displayBrand(data);
      $('#modal').modal('show');
    },
    error: handleAjaxError
  });
}

function resetUploadDialog() {
  // Reset file name
  var $file = $('#brandFile');
  $file.val('');
  $('#brandFileName').html("Choose File");
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
  var $file = $('#brandFile');
  var fileName = $file.val().split("\\").pop();
  $('#brandFileName').html(fileName);
}

function displayUploadData() {
  resetUploadDialog();
  $('#upload-brand-modal').modal('show');
  $('#download-errors').prop('disabled',true);
  $('#process-data').prop('disabled',true);
  $('#brandFile').prop('disabled',false);
}

function displayBrand(data) {
  $("#brandIdInput").val(data.id);
  $("#brandInput").val(data.brand);
  $("#categoryInput").val(data.category);
  $("#modalForm").off("submit").on("submit", function (event) {
    event.preventDefault();
    if ($("#brandIdInput").val()) {
      updateBrand(event);

    } else {
      addBrand(event);
    }
  });
}

function resetModal() {
  $("#modalForm").trigger('reset');
  $("#brandIdInput").val("");
  $("#modalTitle").text("");
  $("#modalSubmitButton").text("");
}

function validateForm() {
  var brandInput = $("#brandInput").val().trim();
    var categoryInput = $("#categoryInput").val().trim();
    var isValid = true;

    if (brandInput.length === 0 || brandInput.length > 30) {
      $("#brandInput").siblings('.invalid-feedback').text('Brand cannot be empty and should be less than 30 characters.').show();
      $("#brandInput").addClass('is-invalid');
      $("#modalSubmitButton").prop('disabled', true);
      return false;
    }
    else{
        $("#brandInput").addClass('is-valid');
    }

    if (categoryInput.length === 0 || categoryInput.length > 30) {
      $("#categoryInput").siblings('.invalid-feedback').text('Category cannot be empty and should be less than 30 characters.').show();
      $("#modalSubmitButton").prop('disabled', true);
      $("#categoryInput").addClass('is-invalid');
      return false;
    }
    else{
        $("#categoryInput").addClass('is-valid');
    }

    $("#modalSubmitButton").prop('disabled', false);

    return true;
}

function activateUpload(){
    $("#process-data").prop('disabled',false);
}

var table;
// INITIALIZATION CODE
function init() {
    role=$("meta[name=role]").attr("content");

    var trgt=[];
    if(role=='supervisor') trgt=[2];
    table = $('#brand_table').DataTable({
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
               $('.dataTables_filter input[type="search"]').on('focus', function() {
                 $(this).addClass('border-info');
               }).on('blur', function() {
                 $(this).removeClass('border-info');
               });
          },
          dom: 'Bfrtip',
          buttons: [],
          language: {
              search: '',
              searchPlaceholder: 'Search...'
          }
      });
        getBrandList();
      // Customize the search input
          var searchInput = $('div.dataTables_filter input');
          // Add shadow effect on focus
          searchInput.on('focus', function() {
              $(this).addClass('shadow')
          });

          // Remove shadow effect on blur
          searchInput.on('blur', function() {
              $(this).removeClass('shadow');
          });

  // Add event listener for submit button in modal
  $('#modalSubmitButton').click(function (event) {
    event.preventDefault();
    if ($("#brandIdInput").val()) {
      updateBrand(event);
    } else {
      addBrand(event);
    }
  });

  // Add event listener for cancel button in modal
  $('#modal').on('hidden.modal', function () {
    resetModal();
  });
  $('#refresh-data').click(getBrandList);
  $('#process-data').click(processData);
  $('#download-errors').click(downloadErrors);
  $('#brandFile').on('change', updateFileName);
    $('#upload-data').click(displayUploadData);
    $('#brandFile').click(activateUpload);

  // Add event listener for upload button
  $('#upload-data').click(function () {
    $('#upload-brand-modal').modal('show');
  });


}

$(document).ready(function () {
$(document).on('input', '#modal .modal-body input', function() {
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
  if(modeType==='edit')checkBrandDataChanged();
});
  init();


});


$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('brands-nav');
    navItem.classList.add('active');
});