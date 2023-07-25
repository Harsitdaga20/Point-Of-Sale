
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
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

function isJson(str){
    try{
        JSON.parse(str);
    }catch(e){
        return false;
    }
    return true;
}



function errormsg(msg){
     toastr.clear();
    toastr.error(msg,"Error").css("width","500px");
}

function successmsg(msg){
     toastr.clear();
    toastr.success(msg,"Success");
}

function handleAjaxError(response) {
    var responseObj = JSON.parse(response.responseText);
    var errorMessage = responseObj.message;
    errormsg(errorMessage);
}


function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr,filename){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob,filename+ '.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', filename+"_"+getCurrentISTDateString()+'.tsv');
    tempLink.click(); 
}
var role;


function init(){
    role=$("meta[name=role]").attr("content");
    if(role=='operator'){
        var d=document.getElementsByClassName("supervisor");
        for(var i=0;i<d.length;i++){
            d[i].style.display='none';
        }
    }
    toastr.options = {
      "closeButton": true,
      "debug": false,
      "newestOnTop": true,
      "progressBar": true,
      "positionClass": "toast-top-right",
      "preventDuplicates": false,
      "onclick": null,
      "showDuration": "300",
      "hideDuration": "1000",
      "timeOut": "5000",
      "extendedTimeOut": "1000",
      "showEasing": "swing",
      "hideEasing": "linear",
      "showMethod": "fadeIn",
      "hideMethod": "fadeOut"
    }
    }
$(document).ready(init);

$(document).ready(function() {
    $('.modal').attr('data-backdrop', 'static');
    $('.dataTables_filter input[type="search"]').on('input', function() {
     var searchValue = $(this).val();
     $(this).prop('value', searchValue);
   });

   $('.dataTables_filter label::before').click(function() {
     var inputElement = $(this).siblings('input[type="search"]');
     inputElement.focus();
   });
   $('.dataTables_filter input[type="search"]').trigger('input');
});
