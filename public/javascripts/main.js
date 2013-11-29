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
	refreshStack: function(data) {
		var opened = new Array()
		$(".panel-collapse.in").each(function () {
			opened.push($(this).attr("id"))
		})
		$('#stackC').addClass("noTransition")
		$('#stackC').html(data.stack)
		if(data.showTab)
			$('#stack a[href="#' + data.showTab + '"]').tab('show')
		for (var i = 0; i < opened.length; i++) {
			var t = $("#" + opened[i])
			if(t.length > 0)
				t.collapse('show')
		}
		
		window.setTimeout(function() {
			$('#stackC').removeClass("noTransition")
		}, 1000);
		
	},
	stdActions: function(data) {
		var type = "success"
		if(data.valid) {
			
		} else {
			type: "danger"
		}
		
		if(data.message) {
			gb.message({
				text: data.message,
				type: type
			});
		}
	}
}
$(function() {
	$("body").on('click', 'input.setFitnessButton', function(){
		$.post('/setFitness', {
			'fitness' : $(this).prev("input.setFitnessInput").val(),
			'name' : $(this).prev("input.setFitnessInput").parents(".cocktail").attr("data-name")
		}, function(data) {
			gb.stdActions(data);
			if(data.valid) {
				gb.refreshStack(data);
			}
		});
	});
	
	$("body").on('click', 'input.pourButton', function(){
		$.post('/pour', {
			'name' : $(this).parents(".cocktail").attr("data-name")
		}, function(data) {
			gb.stdActions(data);
		});
	});

	$('#stack a.tab').click(function(e) {
		e.preventDefault()
		$(this).tab('show')
	})
	
	$("body").on('click', '#refreshB', function(e){
		e.preventDefault()
		//if($(".tab-pane.active").length > 0)
		var tab = $(".tab-pane.active").attr("id")
			
		$.get("/stack" , function(data) {
			gb.stdActions(data);
			if(data.valid) {
				data.showTab = tab;
				gb.refreshStack(data);
			}
		});
	})

	$("body").on('click', '#createForm button', function(e){
		e.preventDefault()
		$.post("/generate", $("#createForm").serialize(), function(data) {
			gb.stdActions(data);
			if(data.valid) {
				gb.refreshStack(data);
			}
		});
	})
	
	$("body").on('click', '#loadTable button', function(e){
		e.preventDefault()
		$.post('/stackOP', {
			'name' 	: $(this).parents("tr").attr("data-name"),
			'action': $(this).attr("data-action"),
		}, function(data) {
			gb.stdActions(data);
			if(data.valid) {
				data.showTab = "_load";
				gb.refreshStack(data);
			}
		});
	})
	
	$('ul.uCButtons a.uCButton').click(function(e) {
		e.preventDefault()
		$.post('/pourType', {
			'name' : $(this).parents("li").attr("data-name")
		}, function(data) {
			gb.stdActions(data);
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
					}
					gb.stdActions(data);
				},
				complete : function() {
					setTimeout(readSerial, 1000);
				}
			});
		})();
	}
});
