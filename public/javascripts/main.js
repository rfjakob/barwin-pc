gb = {
	message: function(m) {
		 $(".alertHeader").hide()
		var t = $(".alert.template").clone()
		t.removeClass("hidden")
		t.removeClass("template")
		t.addClass('alertHeader')
		t.addClass('alert-' + m.type)
		t.children(".message").html(m.text)
		$("body").prepend(t)
		window.setTimeout(function() {
			t.alert('close');
		}, 4000);
	},
	addSerialLine: function(data) {
		var str = '';
		if(data.type == "write")
			str = 'W - '
		if(data.type == "read")
			str = 'R - '
		$('#serialCode').val(str + data.timestamp + " '" + data.string + "'\n" + $('#serialCode').val());
	},
	clearSerialLine: function() {
		$('#serialCode').val('');
	},
	stdActions: function(data) {
		
	}
}
$(function() {
	$("input.fitnessB").click(function() {
		var fitnessI = $(this).prev("input.fitnessI");
		// alert(fitnessI.val());
		$.post('/setFitness', {
			'fitness' : fitnessI.val(),
			'name' : fitnessI.attr("data-name")
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

	$('#serialForm button.send').click(function(e) {
		e.preventDefault()
		$.post("/serial/write", $("#serialForm").serialize(), function(data) {
			if(data.valid) {
				data.type = "write"
				gb.addSerialLine(data);
				gb.message({
					text : "'" + data.string + "' Sent",
					type : "success"
				});
			}
			gb.stdActions(data);
		});
	})

	$('#serialForm button.clear').click(function(e) {
		e.preventDefault()
		gb.clearSerialLine()
	})

	if ($('#serialContainer').length > 0) {
		(function readSerial() {
			$.ajax({
				url: 		'/serial/read',
				success: 	function(data) {
					if(data.valid) {
						if(data.string.length > 0) {
							data.type = "read"
							gb.addSerialLine(data);
						}
					} else {
						gb.message({
							text: data.error,
							type: "danger"
						});
					}
				},
				complete : function() {
					setTimeout(readSerial, 1000);
				}
			});
		})();
	}
});
