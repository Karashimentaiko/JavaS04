'use strict';

var rootUrl = "/java_s04/api/v1.1/expenses";

findAll();

$('#saveExpenses').click(function() {
	$('.error').children().remove();
	if ($('#appId').val() === '') {
		$('.error').append('<div>申請IDは必須入力です。</div>');
	}
	if ($('#repDate').val() === '') {
		$('.error').append('<div>申請日は必須入力です。</div>');
	}
	if ($('#name').val() === '') {
		$('.error').append('<div>申請者は必須入力です。</div>');
	}
	if ($('#title').val() === '') {
		$('.error').append('<div>タイトルは必須入力です。</div>');
	}
	if ($('#payee').val() === '') {
		$('.error').append('<div>支払先は必須入力です。</div>');
	}
	if ($('#price').val() === '') {
		$('.error').append('<div>金額は必須入力です。</div>');
	}
	if ($('.error').children().length != 0) {
		return false;
	}

	var id = $('#id').val()
	if (id === '')
		addExpenses();
	else
		updateExpenses(id);
	return false;
})

$('#newExpenses').click(function() {
	renderDetails({});
});

function findAll(){
	console.log('findAll start.')
	$.ajax({
		type: "GET",
		url: rootUrl,
		dataType: "json",
		success: renderTable
	});
}

function findById(id) {
	console.log('findByID start - id:' + id);
	$.ajax({
		type : "GET",
		url : rootUrl + '/' + id,
		dataType : "json",
		success : function(data) {
			console.log('findById success: ' + data.name);
			renderDetails(data)
		}
	});
}

/*function findByParam() {
	console.log('findByParam start.');

	var urlWithParam = rootUrl + '?postId=' + $('#postIdParam').val()
			+ '&empId=' + $('#empIdParam').val() + '&nameParam='
			+ $('#nameParam').val();
	$.ajax({
		type : "GET",
		url : urlWithParam,
		dataType : "json",
		success : renderTable
	});
}

function addExpenses() {
	console.log('addExpenses start');

	var fd = new FormData(document.getElementById("expensesForm"));

	$.ajax({
		url : rootUrl,
		type : "POST",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			alert('経費の追加に成功しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('経費の追加に失敗しました');
		}
	})
}

function updateExpenses(id) {
	console.log('updateExpenses start');

	var fd = new FormData(document.getElementById("expensesForm"));

	$.ajax({
		url : rootUrl + '/' + id,
		type : "PUT",
		data : fd,
		contentType : false,
		processData : false,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			alert('経費の更新に成功しました');
			findAll();
			renderDetails(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('経費の更新に失敗しました');
		}
	})
}
*/

function deleteById(id) {
	console.log('delete start - id:'+id);
	$.ajax({
		type: "DELETE",
		url: rootUrl+'/'+id,
		success: function() {
			findAll();
			$('#id').val('');
			$('#appId').val('');
			$('#repDate').val('');
			$('#upDate').val('');
			$('#name').val('');
			$('#title').val('');
			$('#payee').val('');
			$('#price').val('');
			$('input[name="exstatus"]').val([ '' ]);
			$('#upName').val('');
		}
	});
}

function renderTable(data) {
	var headerRow = '<tr><th>申請ID</th><th>申請日</th><th>更新日</th><th>申請者</th><th>タイトル</th><th>金額</th><th>ステータス</th>';

	$('#expenses').children().remove();

	if (data.length === 0) {
		$('#expenses').append('<p>現在データが存在していません。</p>')
	} else {
		var table = $('<table>').attr('border', 1);
		table.append(headerRow);

		$.each(data, function(index, expenses) {
			var row = $('<tr>');
			row.append($('<td>').text(expenses.appId));
			row.append($('<td>').text(expenses.repDate));
			row.append($('<td>').text(expenses.upDate));
			row.append($('<td>').text(expenses.name));
			row.append($('<td>').text(expenses.title));
			row.append($('<td>').text(expenses.price));
			row.append($('<td>').text(expenses.exstatus));
			row.append($('<td>').append(
					$('<button>').text("編集").attr("type","button").attr("onclick", "findById("+expenses.id+')')
				));
			row.append($('<td>').append(
					$('<button>').text("削除").attr("type","button").attr("onclick", "deleteById("+expenses.id+')')
				));
			table.append(row);
		});

		$('#expenses').append(table);
	}
}

function renderDetails(expenses) {
	$('.error').text('');
	$('#id').val(expenses.id);
	$('#appId').val(expenses.appId);
	$('#repDate').val(expenses.repDate);
	$('#upDate').val(expenses.upDate);
	$('#name').val(expenses.name);
	$('#title').val(expenses.title);
	$('#payee').val(expenses.payee);
	$('#price').val(expenses.price);
	$('input[name="exstatus"]').val([ expenses.exstatus ]);
	$('#upName').val(expenses.upName);
}

function formToJSON() {
	var appId = $('#appId').val();
	return JSON.stringify({
		"id": (appId == "" ? 0 : appId),
		"name": $('#name').val()
	});
}
