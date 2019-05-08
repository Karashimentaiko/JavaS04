'use strict';

var rootUrl = "/java_s04/api/v1.1/expenses";

initPage();

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

function initPage() {
	var newOption = $('<option>').val(0).text('指定しない').prop('selected', true);
	$('#postIdParam').append(newOption);
	makePostSelection('#postIdParam');
	findAll();
	makePostSelection('#postId');
}

function findByParam() {
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
			row.append($('<td>').text(expenses.repDate);
			row.append($('<td>').text(expenses.upDate);
			row.append($('<td>').text(expenses.name));
			row.append($('<td>').text(expenses.title));
			row.append($('<td>').text(expenses.price));
			row.append($('<td>').text(expenses.status);
			row.append($('<td>').append(
					$('<button>').text("編集").attr("type","button").attr("onclick", "findById("+expenses.id+')')
				));
			row.append($('<td>').append(
					$('<button>').text("詳細").attr("type","button").attr("onclick", "findById("+expenses.id+')')
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
	$('input[name="status"]').val([ expenses.status ]);
	$('#upName').val(expenses.upName);
	$('#rejectReason').val(expenses.rejectReason);
}

function makePostSelection(selectionId, expenses) {
	console.log('makePostSelection start.')
	$.ajax({
		type : "GET",
		url : getPostsUrl,
		dataType : "json",
		success : function(data, textStatus, jqXHR) {
			$.each(data, function(index, post) {
				var newOption = $('<option>').val(post.id).text(post.name);
				if (expenses != null && expenses.post.id == post.id) {
					newOption.prop('selected', isSelected);
				}
				$(selectionId).append(newOption);
			});
		}
	});
}
