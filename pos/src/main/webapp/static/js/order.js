function getProductUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/products";
}

function getOrderUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/orders";
}
var $modalBody = $('.help-body');
var table;
var productData = [];

function openModal(mode, id) {
    initModal();
  var $modal = $('#modal');
  var $modalForm = $('#modalForm');
  if (mode === 'create') {
    $("#orderId").val("");
    $("#modalTitle").text("Create Order");
    $("#modalSubmitButton").text("Create");
    $("#modalSubmitButton").show();
    updateItemTable();
    $modalForm.show();
  } else if (mode === 'edit') {
    $("#orderId").val(id);
    $("#modalTitle").text("Update Order");
    $("#modalSubmitButton").text("Update");
    $("#modalSubmitButton").show();
    getProductData(mode,id);
    $modalForm.show();
  }
  else if (mode === 'view') {
      $("#orderId").val(id);
      $("#modalTitle").text("View Order");
      $modalForm.hide();
      $("#modalSubmitButton").hide();

      var tableHtml =
        '<div class="table-responsive">' +
        '<table id="itemTable" class="table table-striped">' +
        '<thead>' +
        '<tr>' +
        '<th>Product Name</th>' +
        '<th>Selling Price</th>' +
        '<th>Quantity</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<!-- Table rows will be dynamically populated -->' +
        '</tbody>' +
        '</table>' +
        '</div>';
        $modalBody.html(tableHtml);
      getProductData(mode,id);
    }
  $modal.modal('show');
}
function updateTableView(){
  var tableBody = $("#itemTable tbody");
  tableBody.empty();

  $.each(productData, function(index, product) {
    var row =
      "<tr>" +
      "<td>" + product.productName + "</td>" +
      "<td class=\"text-right\">" + (product.sellingPrice ? parseFloat(product.sellingPrice).toFixed(2) : '.00')+ "</td>" +
      "<td>" + product.quantity + "</td>" +
      "</tr>";
    tableBody.append(row);
  });
}

function initModal(){
    $('#barcode').val('').removeClass('is-valid is-invalid');
    $('#sellingPrice').val('').removeClass('is-valid is-invalid');
    $('#quantity').val('').removeClass('is-valid is-invalid');

  // Disable the add button
  $('#addItemBtn').prop('disabled', true);
}

function getProductData(mode,id) {
  var url = getOrderUrl() + "-items/" + id;
  $.ajax({
    url: url,
    type: 'GET',
    success: function(data) {
      productData = []; // Clear the productData array before adding new items
      for (var i in data) {
        var e = data[i];
        var product = {
          barcode: e.barcode,
          productName: e.productName,
          sellingPrice: (e.sellingPrice? parseFloat(e.sellingPrice).toFixed(2) : '.00'),
          quantity: e.quantity
        };
        productData.push(product);
      }
      if(mode==='edit')updateItemTable(); // Update the item table with the fetched data
      else if(mode ==='view') updateTableView();
    },
    error: handleAjaxError
  });
}

function updateItemTable() {
  var tableBody = $("#itemTable tbody");
  tableBody.empty();

  $.each(productData, function(index, product) {
    var row =
      "<tr>" +
      "<td>" + product.barcode + "</td>" +
      "<td>" + product.productName + "</td>" +
      "<td>" + (product.sellingPrice ? parseFloat(product.sellingPrice).toFixed(2) : '.00')+ "</td>" +
      "<td>" +
       '<input type="number" class="form-control form-control-sm quantity-input" value="' + product.quantity + '" />' +
       "</td>" +
      "<td>" +
      '<button class="btn btn-danger btn-sm delete-btn" title="delete"><i class="fa fa-trash"></i></button>' +
      "</td>" +
      "</tr>";
    tableBody.append(row);
  });
  tableBody.on('blur', '.quantity-input', function() {
    var rowIndex = $(this).closest('tr').index();
    var newQuantity = $(this).val();
    newQuantity=+newQuantity;
    $(this).val(newQuantity);
    if (!isNaN(newQuantity) && Number.isInteger(newQuantity) && newQuantity > 0) {
          $(this).removeClass('is-invalid');
          $(this).removeClass('is-valid');
      productData[rowIndex].quantity = newQuantity;
    }
    else {
      $(this).removeClass('is-invalid');
      $(this).removeClass('is-valid');
      $(this).val(productData[rowIndex].quantity);
    }

  });

}

function addItem(event) {

    if (!validateModalForm()) {
        return false;
      }
  var barcode = $("#barcode").val();
  var sellingPrice =$("#sellingPrice").val();
  var quantity = parseInt($("#quantity").val());

  var existingProductIndex = productData.findIndex(function(product) {
    return product.barcode === barcode;
  });

  if (existingProductIndex > -1) {
    errormsg("Order already present in the cart .kindly check it ");
    return false;

  } else {
    var url = getProductUrl();

    $.ajax({
      url: url + "/" + barcode,
      type: "GET",
      success: function(response) {
        var product = {
          barcode: barcode,
          productName: response.productName,
          sellingPrice: sellingPrice,
          quantity: quantity
        };
        if(response.mrp<sellingPrice){
            errormsg("Selling price is greater than the MRP "+response.mrp+". Kindly check it");
            return false;
        }
        productData.push(product);
        updateItemTable();
        clearForm();
        initModal();
      },
      error: handleAjaxError
    });
  }
  return true;
}

function clearForm() {
  $("#barcode").val("");
  $("#sellingPrice").val("");
  $("#quantity").val("");
}

function createOrder(event) {
  var cartItemList = [];
  $.each(productData, function(index, product) {
    var cartItem = {
      barcode: product.barcode,
      sellingPrice: product.sellingPrice,
      quantity: product.quantity
    };
    cartItemList.push(cartItem);
  });
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: 'POST',
    data: JSON.stringify(cartItemList),
    contentType: 'application/json',
    success: function(response) {
      $('#modal').modal('hide');
      successmsg("Order created successfully");
      productData = [];
      cartItemList = [];
      clearForm();
      getOrderList();
    },
    error: handleAjaxError
  });
}

function updateOrder(event,id) {
  var cartItemList = [];
  $.each(productData, function(index, product) {
    var cartItem = {
      barcode: product.barcode,
      sellingPrice: product.sellingPrice,
      quantity: product.quantity
    };
    cartItemList.push(cartItem);
  });
    if (cartItemList.length === 0) {
        errormsg("cannot update order with an empty item list")
        return false;
      }
  var url = getOrderUrl()+"/"+id;
  $.ajax({
    url: url,
    type: 'PUT',
    data: JSON.stringify(cartItemList),
    contentType: 'application/json',
    success: function(response) {
        successmsg("Order Updated Successfully")
      $('#modal').modal('hide');
      productData = [];
      cartItemList = [];
    },
    error: handleAjaxError
  });
}

function resetModal() {
  $("#modalForm").trigger('reset');
  $modalBody.html('');
  $("#orderId").val("");
  $("#modalTitle").text("");
  $("#modalSubmitButton").text("");
  productData = [];
}

function getOrderList() {
  table.clear().draw();
  table.row.add(["","","<i class='fa fa-refresh fa-spin'></i>","",""]).draw();
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: 'GET',
    success: function(data) {
      displayOrderList(data);
    },
    error: handleAjaxError
  });
}

function formatZonedDateTime(zonedDateTime) {
  zonedDateTime *= 1000;
  var dateObject = new Date(zonedDateTime);
  var formattedDateTime = dateObject.toLocaleString();
  return formattedDateTime;
}

function displayOrderList(data) {
  var $tbody = $('#order-table').find('tbody');
  table.clear().draw();
  var dataRows=[];
  for (var i in data) {
    var e = data[i];
    var editButton = '<button class="btn btn-primary mr-2" onclick="openModal(\'edit\', ' + e.orderId + ')" title="edit"><i class="fa fa-pencil"></i></button>';
    var editButtonDisabled='<button class="btn btn-primary mr-2" onclick="openModal(\'edit\', ' + e.orderId + ')" disabled><i class="fa fa-pencil"></i></button>';
    var viewButton= '<button class="btn btn-info mr-2" onclick="openModal(\'view\', ' + e.orderId + ')" title="view"><i class="fa fa-eye"></i></button>';
    var invoiceButton= '<button class="btn btn-success" onclick="generateInvoice(' + e.orderId + ')" title="generate invoice"><i class="fa fa-clipboard" title="generate invoice"></i></button>';
    var downloadButton= '<button class="btn btn-secondary" onclick="downloadInvoice(' + e.orderId + ')" title="download invoice"><i class="fa fa-download"></i></button>';
    var row;
    var forCreated='<div class="button-row">'+viewButton +editButton+ invoiceButton + '</div>';
    var forInvoiced='<div class="button-row">'+viewButton +editButtonDisabled+downloadButton+ '</div>';
    if(e.status ==='created'){
        row=[
        e.orderCode,
        formatZonedDateTime(e.orderCreatedTime),
        "-",
        '<span class="mode mode_on">Created</span>',
        forCreated
        ];
    }
    else{
    row=[
            e.orderCode,
            formatZonedDateTime(e.orderCreatedTime),
            formatZonedDateTime(e.orderInvoicedTime),
            '<span class="mode mode_off">Invoiced</span>',
            forInvoiced
            ];
    }
    dataRows.push(row);
  }
  table.rows.add(dataRows).draw();
}


function generateInvoice(id){
    var url=getOrderUrl()+"/invoice/"+id;
    $.ajax({
        url: url,
        type: 'PUT',
        success: function(response) {
            successmsg("order invoiced successfully");
            getOrderList();
            downloadInvoice(id);
        },
        error: handleAjaxError
    })
}
function getCurrentISTDateString() {
  // Get the current date
  const now = new Date();

  const ISTOffset = 5.5 * 60;
  const ISTTime = new Date(now.getTime() + ISTOffset * 60 * 1000);

  const options = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    timeZone: "Asia/Kolkata",
  };
  return ISTTime.toLocaleString("en-IN", options);
}

function downloadInvoice(code){
    var url=$("meta[name=baseUrl]").attr("content")+"/api/invoice/"+code;
    $.ajax({
        url:url,
        type:'GET',
        xhrFields: {
           responseType: 'blob'
         },
         success: function(response) {
           var blob = new Blob([response], { type: 'application/pdf' });
           var link = document.createElement('a');
           link.href = window.URL.createObjectURL(blob);
           link.download = "Invoice_" +code +"_"+getCurrentISTDateString()+".pdf";
           link.click();
           successmsg("Invoice Downloaded");
         },
        error:handleAjaxError
    })
}

function validateModalForm() {
  var barcode = $("#barcode").val().trim();
  var sellingPrice =$("#sellingPrice").val();
  var quantity =$("#quantity").val();
  quantity=+quantity;

  if (barcode === '') {
    $("#barcode").addClass('is-invalid');
    $("#barcode").siblings('.invalid-feedback').text('Please provide a Barcode for product.');
    $('#addItemBtn').prop('disabled', true);
    return false;
  }
  else{
    $("#barcode").addClass('is-valid');
  }
  if (!Number.isInteger(quantity) || quantity < 1 || quantity > 100000000) {
      $("#quantity").addClass('is-invalid');
      $("#quantity").siblings('.invalid-feedback').text('Quantity should be lower than 100000000 and greater than 0');
      $('#addItemBtn').prop('disabled', true);
      return false;
   }
   else{
      $("#quantity").addClass('is-valid');
   }
  if (isNaN(sellingPrice) || sellingPrice < 0 || sellingPrice > 100000000) {
    $("#sellingPrice").addClass('is-invalid');
    $("#sellingPrice").siblings('.invalid-feedback').text('Selling price should be lower than 100000000 and non-negative');
    $('#addItemBtn').prop('disabled', true);
    return false;
  }
  else{
     $("#sellingPrice").addClass('is-valid');
  }



  $('#addItemBtn').prop('disabled', false);
  $("#quantity").val(quantity);
  return true;
}

function init() {
  table = $('#order-table').DataTable({
    columnDefs: [
        {
            targets: -1,
            orderable: false,
            width:"20%"
        },
        {
            targets: 0,
            orderable:false
        }
    ],
    order: [[1, 'asc']],
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
  $('#refresh-data').click(getOrderList);

  $("#addItemBtn").click(addItem);

  $('#modalSubmitButton').click(function(event) {
    event.preventDefault();
    if ($("#orderId").val()) {
      updateOrder(event,$("#orderId").val());
    } else {
      createOrder(event);
    }
  });

  $('#modal').on('hidden.bs.modal', function() {
    resetModal();
  });

  $(document).on("click", ".delete-btn", function() {
    var row = $(this).closest("tr");
    var index = row.index();
    productData.splice(index, 1);
    updateItemTable();
  });

}

$(document).ready(function() {

  $(document).on('input', '#modal .modal-body input', function() {
    $("#barcode").siblings('.invalid-feedback').text('Please provide a Barcode for product.');
    $("#quantity").siblings('.invalid-feedback').text('Please provide a Quantity of product.');
    $("#sellingPrice").siblings('.invalid-feedback').text('Please provide a Selling Price.');


    var $input = $(this);
    if ($input.val().trim() !== '') {
      $input.removeClass('is-invalid');
      $input.siblings('.invalid-feedback').hide();
      $input.addClass('is-valid');
    } else {
      $input.addClass('is-invalid');
      $input.siblings('.invalid-feedback').show();
    }
    validateModalForm();
  });

  init();
  getOrderList();
});

$(document).ready(function(){
    const navbarContainer = document.getElementById('navbarContainer');
    const navItems = navbarContainer.querySelectorAll('.navbar-nav > .nav-item');
    navItems.forEach(item => {
       item.classList.remove('active')});
    const navItem = document.getElementById('orders-nav');
    navItem.classList.add('active');
});