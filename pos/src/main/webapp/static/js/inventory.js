var inventoryList=[];
var table;
var role;
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getSupervisorInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/supervisor/inventory";
}

function arrayToJson(){
    let json=[];
    for(i in inventoryList){
    let d={};
    d["barcode"]=JSON.parse(inventoryList[i]).barcode;
    d["quantity"]=JSON.parse(inventoryList[i]).quantity;
    json.push(d);
    }
    return JSON.stringify(json);
}


function updateInventory(event){
    if (!validateForm()) {
        return false;
      }
	//Get the ID
	var id = $("#inventory-edit-form input[name=id]").val();
	var url = getSupervisorInventoryUrl();

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);
    inventoryList.push(json);
    var jsonObj=arrayToJson();
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: jsonObj,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	           inventoryList=[];
	           successmsg("Inventory Updated Successfully");
	           $("#inputQuantity").removeClass('is-invalid');
               $("#inputQuantity").siblings('.invalid-feedback').hide();
               $("#inputQuantity").addClass('is-valid');
	   		getInventoryList();
	   		$('#edit-inventory-modal').modal('hide');
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
               inventoryList=[];
           }
	});
	return false;
}


function getInventoryList(){
    table.clear().draw();
  table.row.add(["","<i class='fa fa-refresh fa-spin'></i>","",""]).draw();
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}


// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];
	if(file.name.split('.').pop()!="tsv"){
	errormsg("File is not a tsv file");
      return;
      }
      else{
        readFileData(file, readFileDataCallback);
      }
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
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

function uploadRows(){
	//Update progress
	updateUploadDialog();
      var json = JSON.stringify(fileData);
      $("#process-data").prop("disabled", true).find(".fa-refresh").show();
      var headers=["barcode","quantity"];
      var jsond=JSON.parse(json);
      if(Object.keys(jsond[0]).length !=headers.length){
           errormsg("File column numbers do not match");
        return;
      }
      for(var i in headers){
       if(!jsond[0].hasOwnProperty(headers[i])){
       errormsg("File headers do not match. Kindly check sample file");
        return;
       }
      }
    var url=getSupervisorInventoryUrl();
	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		errorData=response;
            processCount=fileData.length;
            getInventoryList();
            successmsg("Inventory Uploaded Successfully");
	   },
	   error:  function (response) {
        if(response.status ==403){
        errormsg("403 unauthorized");
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
    ("#inventoryFile").prop('disabled',true);
}

function downloadErrors(){
	writeFileData(errorData,'inventory-upload-errors');
}

//UI DISPLAY METHODS

function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	var dataRows=[];
	table.clear().draw();
    for(var i in data){
        var e = data[i];
        var serialNumber=parseInt(i)+1;
        var buttonHtml='';
        if(role=='supervisor'){
            buttonHtml+=' <button class="btn btn-primary" onclick="displayEditInventory(' + e.id + ')" title="edit"><i class="fa fa-pencil"></i></button>';
        }
        dataRows.push([
          e.productName,
          e.barcode,
          e.quantity,
          buttonHtml
          ]);
    }
    table.rows.add(dataRows).draw();
}

function displayEditInventory(id){
	var url = getSupervisorInventoryUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#inventoryFile');
	var fileName = $file.val().split("\\").pop();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
	$('#download-errors').prop('disabled',true);
      $('#process-data').prop('disabled',true);
      $('#inventoryFile').prop('disabled',false);
}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$("#inventory-edit-form input[name=id]").val(data.id);
	$('#edit-inventory-modal').modal('toggle');
}

function validateForm() {
  var quantity = $("#inventory-edit-form input[name=quantity]").val();
  quantity=+quantity;

  if (quantity === '') {
    $("#inventory-edit-form inputQuantity").addClass('is-invalid');
    $("#inventory-edit-form inputQuantity").siblings('.invalid-feedback').text('Please provide Quantity of product.').show();

    return false;
  }

  if (!Number.isInteger(Number(quantity))) {
      $("#inputQuantity").addClass('is-invalid');
      $("#inputQuantity").siblings('.invalid-feedback').text('Please provide Quantity as an Integer.').show();

      return false;
    }

    var quantityValue = parseInt(quantity);
    if (isNaN(quantityValue) || quantityValue < 0 || quantityValue > 100000000) {
        $("#inputQuantity").addClass('is-invalid');
      $("#inputQuantity")
            .siblings('.invalid-feedback')
            .text('Please provide Quantity greater than 0 and less than 100000000.').show();
      return false;
    }
    $("#inventory-edit-form input[name=quantity]").val(quantity);
  return true;
}


//INITIALIZATION CODE
function init(){
    role=$("meta[name=role]").attr("content");
    var trgt=[1];
    if(role=='supervisor') trgt=[1,3];
    table=$('#inventory-table').DataTable({
       columnDefs: [
         {
           targets: trgt,
           orderable: false,
         },
         {
           targets: '_all',
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
       },
       dom: 'Bfrtip',
       buttons: [],
       language: {
           search: '',
           searchPlaceholder: 'Search...'
       }
   });

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
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#inventoryFile').click(activateUpload);
    $('#inventoryFile').on('change', updateFileName);

    $('#upload-data').click(function () {
        $('#upload-inventory-modal').modal('show');
      });
}

function activateUpload(){
    $("#process-data").prop('disabled',false);
}


$(document).ready(function (){
    $("#iinputQuantity").siblings('.invalid-feedback').text('Please provide a Quantity of product.');
    $(document).on('input', function() {
      var $input = $("#inputQuantity");
      if ($input.val().trim() !== '') {
        $input.removeClass('is-invalid');
        $input.siblings('.invalid-feedback').hide();
        $input.addClass('is-valid');
      } else {
        $input.addClass('is-invalid');
        $input.siblings('.invalid-feedback').show();
      }
    });
    $('#edit-inventory-modal').on('shown.bs.modal', function() {
      $(this).find('input').addClass('is-valid').removeClass('is-invalid');
    });
    $('#edit-inventory-modal').on('hidden.bs.modal', function() {
      $(this).find('input').removeClass('is-valid').removeClass('is-invalid');
    });
});

$(document).ready(init);
$(document).ready(getInventoryList);

$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('brands-nav');
    navItem.classList.add('active');
});

$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('inventory-nav');
    navItem.classList.add('active');
});