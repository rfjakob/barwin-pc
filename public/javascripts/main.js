$(function() {
	$("input.fitnessB").click(
			function() {
				var fitnessI = $(this).prev("input.fitnessI");
				//alert(fitnessI.val());
				$.post('/setFitness',
						{	'fitness': 	fitnessI.val(),
							'id':		fitnessI.attr("data-id")},
						function(data) {
							window.alert(data);
						});
			});
});