
function getReportUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/supervisor/reports";
}

function getBrandList() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  var url = baseUrl + "/api/brands";
  $.ajax({
    url: url,
    type: 'GET',
    success: function (data) {
      dropdown(data, "brandSelect1", "categorySelect1");
      dropdown(data, "brandSelect2", "categorySelect2");
    },
    error: handleAjaxError
  });
}

function dropdown(data, brandSelectId, categorySelectId) {
  var brandData = {};
  for (var i in data) {
    var brands = data[i];
    if (brandData.hasOwnProperty(brands.brand)) {
      brandData[brands.brand].push(brands.category);
    } else {
      brandData[brands.brand] = [brands.category];
    }
  }
  var brandSel = document.getElementById(brandSelectId);
  var categorySel = document.getElementById(categorySelectId);
  brandSel.length = 1;
  for (var brand in brandData) {
    brandSel.options[brandSel.options.length] = new Option(brand, brand);
  }
  brandSel.onchange = function () {
    var selected = this.value;
    categorySel.length = 1;
    for (var d = 0; d < brandData[selected].length; d++) {
      categorySel.options[categorySel.options.length] = new Option(brandData[selected][d], brandData[selected][d]);
    }
  }
}



function initForm(formId) {
  $(formId).find('input').val('').removeClass('is-valid is-invalid');
  $(formId).find('select').val('').removeClass('is-valid is-invalid');
  $(formId).find('.invalid-feedback').hide();
  $(formId).find('.btn').prop('disabled', true);
}


  function submitFormAndDownloadCSV(json, apiUrl,fileName,formId) {
    $.ajax({
      type: 'POST',
      url: apiUrl,
      data: json,
      contentType: 'application/json',
      xhrFields: {
        responseType: 'blob' // Set the responseType to 'blob' to handle binary data
      },
      success: function(response) {
        var downloadLink = document.createElement('a');
      var blobUrl = URL.createObjectURL(response);
      downloadLink.href = blobUrl;
      var currentDate = new Date();
      var formattedDate = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
      var formattedTime = currentDate.getHours() + '-' + currentDate.getMinutes() + '-' + currentDate.getSeconds();
      downloadLink.download = fileName+'Report_'+formattedDate+'_'+formattedTime+'.csv';
      downloadLink.click();
      URL.revokeObjectURL(blobUrl);
      successmsg(fileName+" Report downloaded");
      $(formId)[0].reset();
      initForm(formId);
      },
      error: handleAjaxError
    });
  }

function validateInventoryForm() {
    var brand = $('#inventory-form #brandSelect1').val().trim();
    var category= $('#inventory-form #categorySelect1').val().trim();
    if(brand.length >30 || brand.length===0){
        $('#inventory-form #brandSelect1').addClass('is-invalid');
        $('#inventory-form #brandSelect1').siblings('.invalid-feedback').text('Please select brand').show();
        return false;
    }
    else{
        $('#inventory-form #brandSelect1').removeClass('is-invalid');
        $('#inventory-form #brandSelect1').addClass('is-valid');
    }
     if(category.length >30 || category.length===0){
         $('#inventory-form #categorySelect1').addClass('is-invalid');
         $('#inventory-form #categorySelect1').siblings('.invalid-feedback').text('Please select category').show();
         return false;
     }
     else{
          $('#inventory-form #categorySelect1').removeClass('is-invalid');
          $('#inventory-form #categorySelect1').addClass('is-valid');
     }

    return true;
  }


  // Function to validate the Daily Sales form
  function validateDailySalesForm() {
    var startDate = $('#daily-sales-form #start').val();
    var endDate = $('#daily-sales-form #end').val();
    var currentDate = new Date().toISOString().split('T')[0];
    // Validate start date and end date fields
    if (startDate === '' ) {
        $('#daily-sales-form #start').addClass('is-invalid');
        $('#daily-sales-form #start').siblings('.invalid-feedback').text('Please choose the date').show();
      return false;
    }
    else{
            $('#daily-sales-form #start').removeClass('is-invalid');
            $('#daily-sales-form #start').addClass('is-valid');
    }
    if (endDate === '' ) {
        $('#daily-sales-form #end').addClass('is-invalid');
        $('#daily-sales-form #end').siblings('.invalid-feedback').text('Please choose the date').show();
      return false;
    }
    else{
      $('#daily-sales-form #end').removeClass('is-invalid');
      $('#daily-sales-form #end').addClass('is-valid');
    }
      if(startDate>=currentDate){
          $('#daily-sales-form #start').addClass('is-invalid');
          $('#daily-sales-form #start').siblings('.invalid-feedback').text('Please choose date below current date').show();
        return false;
       }
       else{
            $('#daily-sales-form #start').removeClass('is-invalid');
            $('#daily-sales-form #start').addClass('is-valid');
       }
       if(endDate>=currentDate){
           $('#daily-sales-form #end').addClass('is-invalid');
           $('#daily-sales-form #end').siblings('.invalid-feedback').text('Please choose date below current date').show();
         return false;
        }
        else{
            $('#daily-sales-form #end').removeClass('is-invalid');
            $('#daily-sales-form #end').addClass('is-valid');
        }
        if(startDate > endDate){
        $('#daily-sales-form #start').addClass('is-invalid');
        $('#daily-sales-form #start').siblings('.invalid-feedback').text('Start date should be less than or equal to end date').show();
      return false;
     }
     else{
        $('#daily-sales-form #start').removeClass('is-invalid');
        $('#daily-sales-form #start').addClass('is-valid');
     }
    return true;
  }

  // Function to validate the Revenue form
  function validateRevenueForm() {
    var brand = $('#revenue-form #brandSelect2').val().trim();
    var category = $('#revenue-form #categorySelect2').val().trim();
    var startDate = $('#revenue-form #start').val();
    var endDate = $('#revenue-form #end').val();
    var currentDate = new Date().toISOString().split('T')[0];

    // Validate brand, category, start date, and end date fields
    if(brand.length >30 || brand.length===0){
        $('#revenue-form #brandSelect2').addClass('is-invalid');
        $('#revenue-form #brandSelect2').siblings('.invalid-feedback').text('Select the brand').show();
        return false;
    }
    else{
            $('#revenue-form #brandSelect2').removeClass('is-invalid');
       $('#revenue-form #brandSelect2').addClass('is-valid');
    }
     if(category.length >30 || category.length===0){
         $('#revenue-form #categorySelect2').addClass('is-invalid');
         $('#revenue-form #categorySelect2').siblings('.invalid-feedback').text('Select the category').show();
         return false;
     }
     else{
        $('#revenue-form #categorySelect2').removeClass('is-invalid');
        $('#revenue-form #categorySelect2').addClass('is-valid');
     }
    if (startDate === '' ) {
        $('#revenue-form #start').addClass('is-invalid');
        $('#revenue-sales-form #start').siblings('.invalid-feedback').text('Please choose the date').show();
      return false;
    }
    else{
            $('#revenue-form #start').removeClass('is-invalid');
       $('#revenue-form #start').addClass('is-valid');
    }
    if (endDate === '' ) {
        $('#revenue-form #end').addClass('is-invalid');
        $('#revenue-form #end').siblings('.invalid-feedback').text('Please choose the date').show();
      return false;
    }else{
         $('#revenue-form #end').removeClass('is-invalid');
        $('#revenue-form #end').addClass('is-valid');
    }
     if(startDate>=currentDate){
        $('#revenue-form #start').addClass('is-invalid');
        $('#revenue-form #start').siblings('.invalid-feedback').text('Please choose date below current date').show();
      return false;
     }else{
         $('#revenue-form #start').removeClass('is-invalid');
         $('#revenue-form #start').addClass('is-valid');
     }
     if(endDate>=currentDate){
         $('#revenue-form #end').addClass('is-invalid');
         $('#revenue-form #end').siblings('.invalid-feedback').text('Please choose date below current date').show();
       return false;
      }else{
               $('#revenue-form #end').removeClass('is-invalid');
               $('#revenue-form #end').addClass('is-valid');
      }
      if(startDate > endDate){
      $('#revenue-form #start').addClass('is-invalid');
      $('#revenue-form #start').siblings('.invalid-feedback').text('Start date should be less than or equal to end date').show();
    return false;
   }else{
         $('#revenue-form #start').addClass('is-valid');

   }
    return true;
  }


$(document).ready(function() {
    $('#brand-form').on('click', '.btn',function(e) {
        e.preventDefault();
        var url=getReportUrl()+"/brand";
        $.ajax({
            url: url,
            type: 'GET',
            xhrFields: {
              responseType: 'blob' // Set the responseType to 'blob' to handle binary data
            },
            success: function(response) {
              var downloadLink = document.createElement('a');
              var blobUrl = URL.createObjectURL(response);
              var currentDate = new Date();
              var formattedDate = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
              var formattedTime = currentDate.getHours() + '-' + currentDate.getMinutes() + '-' + currentDate.getSeconds();
              downloadLink.href = blobUrl;
              downloadLink.download = 'BrandReport_'+formattedDate+'_'+formattedTime+'.csv';
              downloadLink.click();
              URL.revokeObjectURL(blobUrl);
              successmsg("Brand Report Downloaded");
            },
            error: handleAjaxError
          });
  });

  $('#inventory-form').on('click', '.btn',function(e) {

    e.preventDefault();
    if (validateInventoryForm()) {
        var $form = $("#inventory-form");
        var json = toJson($form);
        var url=getReportUrl()+"/inventory"
        submitFormAndDownloadCSV(json,url,'Inventory','#inventory-form');
    }
  });

  $('#daily-sales-form').on('click', '.btn',function(e) {

    e.preventDefault();
    if (validateDailySalesForm()) {
        var $form = $("#daily-sales-form");
        var startDate = $form.find('input[name="start"]').val()+'T00:00:00+05:30';
        var endDate = $form.find('input[name="end"]').val()+'T23:59:59+05:30';
        var json={
            start:startDate,
            end:endDate
        }
        json=JSON.stringify(json);
        var url=getReportUrl()+"/daily-sales";
      submitFormAndDownloadCSV(json,url,'DailySales','#daily-sales-form');
    }
  });

  $('#revenue-form').on('click', '.btn',function(e) {
    e.preventDefault();
    if (validateRevenueForm()) {
        var $form = $("#revenue-form");
        var startDate = $form.find('input[name="start"]').val()+'T00:00:00+05:30';
        var endDate = $form.find('input[name="end"]').val()+'T23:59:59+05:30';
            var json ={
            start:startDate,
            end:endDate,
            brand:$('#revenue-form #brandSelect2').val().trim(),
            category:$('#revenue-form #categorySelect2').val().trim()
            }
            json=JSON.stringify(json);
        var url=getReportUrl()+"/revenue";
      submitFormAndDownloadCSV(json,url,'Revenue','#revenue-form');
    }
  });
});



$(document).ready(function() {
    var today = new Date();
       today.setDate(today.getDate() - 1);
       var yesterday = today.toISOString().split('T')[0];
       $('input[type="date"]').attr('max', yesterday);
       getBrandList();
    initForm('#inventory-form');
    initForm('#daily-sales-form');
    initForm('#revenue-form');
   $(document).on('input change', '#inventory-form input, #inventory-form select', function() {
     if (validateInventoryForm()) {
       $('#inventory-form .btn').prop('disabled', false);
     } else {
       $('#inventory-form .btn').prop('disabled', true);
     }
   });

   $(document).on('input', '#daily-sales-form input', function() {
     if (validateDailySalesForm()) {
       $('#daily-sales-form .btn').prop('disabled', false);
     } else {
       $('#daily-sales-form .btn').prop('disabled', true);
     }
   });

   $(document).on('input', '#revenue-form input', function() {
     if (validateRevenueForm()) {
       $('#revenue-form .btn').prop('disabled', false);
     } else {
       $('#revenue-form .btn').prop('disabled', true);
     }
   });
 });


$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('reports-nav');
    navItem.classList.add('active');
});