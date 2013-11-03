gb = {
	message: function(m) {
		var t = $(".alert.template").clone()
		t.removeClass("hidden")
		t.removeClass("template")
		t.addClass('alert-' + m.type)
		t.children(".message").html(m.text)
		$("body").prepend(t)
		window.setTimeout(function() {
			t.alert('close');
		}, 6000);
	},
	addSerialLine: function(data) {
		var str = '';
		if(data.type == "write")
			str = '>>> '
		if(data.type == "read")
			str = '<<< '
		$('#serialCode').val(str + data.timestamp + " " + data.string + $('#serialCode').val());
	}
}
$(function() {
	$("input.fitnessB").click(function() {
		var fitnessI = $(this).prev("input.fitnessI");
		// alert(fitnessI.val());
		$.post('/setFitness', {
			'fitness' : fitnessI.val(),
			'id' : fitnessI.attr("data-id")
		}, function(data) {
			window.alert(data);
		});
	});

	$('#stacks a.tab').click(function(e) {
		e.preventDefault()
		$(this).tab('show')
	})

	$('#createForm button').click(function(e) {
		e.preventDefault()
		$.post("/generate", $("#createForm").serialize(), function() {
			gb.message({
				text : "Generated",
				type : "success"
			});
		});
	})

	$('#serialForm button').click(function(e) {
		e.preventDefault()
		$.post("/serial/write", $("#serialForm").serialize(), function(data) {
			data.type = "write"
			gb.addSerialLine(data);
			gb.message({
				text : "Sent",
				type : "success"
			});
			
		});
	})

	if ($('#serialContainer').length > 0) {
		(function readSerial() {
			$.ajax({
				url : '/serial/read',
				success : function(data) {
					if(data.string.length > 0) {
						data.type = "read"
						gb.addSerialLine(data);
					}
				},
				complete : function() {
					setTimeout(readSerial, 1000);
				}
			});
		})();
	}
});
