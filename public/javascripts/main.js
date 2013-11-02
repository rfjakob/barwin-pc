$(function() {
	$("input.fitnessB").click(function() {
		var fitnessI = $(this).prev("input.fitnessI");
		//alert(fitnessI.val());
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
		$.post("/generate", $( "#createForm" ).serialize() );
	})

});

