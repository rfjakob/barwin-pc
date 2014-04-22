	var VoteValue = {
	"euro":0,
	"cent":0
	 }
	var LastPadKeyPressed = {
	"euros":"INIT",
	"cents":"INIT"
	}
	var statusCode = -1;
	var statusMessage = "";
	var statusCocktail = "";
	
	var effectTime = 500;
	var lastStatusCode = -1;
	
	var c = 0;
	
	function eurosKeyPressed(id) {
		if(LastPadKeyPressed.euros != id){
			if(LastPadKeyPressed.euros=='INIT'){centsKeyPressed('#Voteoo');}
			$(LastPadKeyPressed.euros).css({'background-color': 'white'});
			$(LastPadKeyPressed.euros+"a").css({'color': 'black'});
			$(id).css({'background-color': 'black'});
			$(id+"a").css({'color': 'white'});
			if(id=="#Vote10"){centsKeyPressed('#Voteoo');}
			LastPadKeyPressed.euros = id;
		}
	}

	function centsKeyPressed(id) {
		if(LastPadKeyPressed.euros != '#Vote10'){
			if(LastPadKeyPressed.cents != id){
				if(LastPadKeyPressed.euros=='INIT'){
					$('#Voteo').css({'background-color': 'black'});
					$('#Voteoa').css({'color': 'white'});
					$('#PleaseVoteLowImage').fadeTo( 'fast' , 1.0 );
					$('#PleaseVoteHighImage').fadeTo( 'fast' , 0.0 );
					LastPadKeyPressed.euros = "#Voteo";
				}
				$(LastPadKeyPressed.cents).css({'background-color': 'white'});
                		$(LastPadKeyPressed.cents+"a").css({'color': 'black'});
                		$(id).css({'background-color': 'black'});
				$(id+"a").css({'color': 'white'});
				LastPadKeyPressed.cents = id;
			}
        	}
	}

	(function updateStatus() {
		$.ajax({
				url: 		'/getStatus/' + Math.random().toString(36).substring(7),
				success: 	function(data) {
					if(data.valid) {
						statusCode = data.statusCode;
						statusMessage = data.statusMessage;
						statusCocktail = data.statusCocktail;
						//console.log("status: " + data.statusCode + " " + data.statusCocktail + " " + $("#OrderedCocktailName").html());
						//$(".barwin").html(data.statusCode + " " + c++)
					}
				},
				complete : function() {
					setTimeout(updateStatus, 1000);
				}
			});
		})();

	function AnimateRotate(angle) {
		var $elem = $('#PleaseWaitImage');
		$({deg: 0}).animate({deg: angle}, {
    		duration: 3000,
    		step: function(now) {
        		$elem.css({
            		transform: 'rotate(' + now + 'deg)'
        		});
    		}
		});
	}

	function AnimateThanks(opacity) {
		var $elem = $('#ThanksMessage');
		$({val: 0}).animate({val: opacity}, {
    		duration: 3000,
    		step: function(now) {
        		$elem.css({
            		transform: 'opacity(' + now + 'val)'
        		});
    		}
		});
	}
	
	var timesTo = 0;
	var pouring = false;
	function checkStatus() {
		//console.log("checkStatus: "  + statusCode);
		/* status
		 * 0 ready
		 * 1 waiting for cup > #mix
		 * 2 pouring > #mix, replace message to Preparing
		 * 3 take cup
		 */
		if(timesTo == 0) {
			AnimateRotate(360);
			timesTo = 6;
		}

		// CHECK IF IT IS THE RIGHT COCKTAIL IS POURING
		// ONLY IF IT IS THE ORDERED, CHECK statusCode
		/*if(statusCocktail != $("#OrderedCocktailName").html()) {
			timesTo--;
			setTimeout(checkStatus, 1000)
		} else {*/
			if (statusCode == 0) {
				if(pouring) {
					window.location.href = "#vote";
					pouring = false;
				} else {
					timesTo--;
					setTimeout(checkStatus, 1000)
				}
			} else if (statusCode == 1){
				if(lastStatusCode != statusCode)
					showMessage("Please place the cup on the <br/> &#9679;");
				//window.location.href = "#mix";
				setTimeout(checkStatus, 1000)
			} else if (statusCode == 2){
				
				$("#MixingLayer .SystemMessage").html(statusMessage);
				timesTo--;
				window.location.href = "#mix2"; // implement error page
				pouring = true;
				setTimeout(checkStatus, 1000)
					
			} else if (statusCode == 3){
				if(lastStatusCode != statusCode)
					showMessage("Please taste your cocktail and rate it!");
				setTimeout(checkStatus, 1000)
			} else{
				setTimeout(checkStatus, 1000)
			}
			lastStatusCode = statusCode
		//}
	}

	function vote(voteValue) {
		//alert(voteValue)
		$.post('/setFitness', {
			'fitness' : voteValue,
			'name' : $("#OrderedCocktailName").html()
		}, function(data) {
			showMessage("Thank you and have a nice drink!");
			setTimeout(function() {
				window.location.href = "#order"
				window.location.reload(true);
			}, 3000);
		});
	}

	function pour(nr) {
		$("#OrderedCocktailNumber").html(nr);
		document.getElementById("ChosenCocktail").innerHTML = $("#Cocktail" + nr + " a").html();
		document.getElementById("CocktailIngredients").innerHTML = "<br />" + $("#Cocktail" + nr).attr("data-ingredients");
		window.location.href = "#confirm";
	}
	
	function showMessage(message) {
		$(".layer").hide();
		$(".layer").css("opacity",'0');
		$("#MessageLayer .messageContent span").html(message);
		$("#MessageLayer").show();
		$("#MessageLayer").animate({opacity:'1'}, 200);
	}
	
	function readAndSetStep(){
    	var step = (window.location.hash).replace('#', '');
    	if (step.length !== 0) {
    		$(".layer").hide();
    		$(".layer").css("opacity",'0');
        	if (step == "order") {
        		$("#OrderLayer").show();
        		$("#OrderLayer").animate({opacity:'1'}, effectTime);
        	} else if (step == "confirm") {
        		$("#ConfirmLayer").show();
        		$("#ConfirmLayer").animate({opacity:'1'}, effectTime);
        		$('#Page').scrollTop(0);
			} else if (step == "mix"){
				$("#MixingLayer").show();
				$("#MixingLayer").animate({opacity:'1'}, effectTime);

				$.post('/pourType', {
					'name' : $("#Cocktail" + $("#OrderedCocktailNumber").html()).attr("data-name")
				}, function(data) {
					 $("#OrderedName").html(data.name)
					 $("#OrderedCocktailName").html(data.cocktailName)					
				});

				checkStatus();
			} else if (step == "mix2"){
				$("#MixingLayer").show();
				$("#MixingLayer").animate({opacity:'1'}, effectTime);
			} else if (step == "vote"){
				$("#VotingQuestion").html('Please rate for this ' + $("#OrderedName").html() + '?');				
				$("#VotingLayer").show();
				$("#VotingLayer").animate({opacity:'1'}, effectTime);

				$("#VotingValueDisplayContent").val(7.5); 
				$('#Page').scrollTop(0);
			} else {
				window.location.href = "#order"
			}
    	}
	}
	$(window).on('hashchange', function (){
		readAndSetStep();
		$("#Page").focus();
	});
	var screenHeight = window.screen.availHeight;
	var screenWidth = window.screen.availWidth;
	$(document).ready(function(){
		
	});
	window.onload = function() {
		readAndSetStep();
		if ($(window).height() < 963){
			
		}
		else if ($(window).height() < 483){
			$("@-webkit-viewport").animate({width: screenHeight});
			$("@-moz-viewport").animate({width: screenHeight-30});
			$("@-o-viewport").animate({width:screenHeight});
			document.getElementById("Page").style.marginTop = "auto";
			document.getElementById("Page").style.marginRight = "auto";
			document.getElementById("Page").style.marginBottom = "auto";
			document.getElementById("Page").style.marginLeft = "auto";
		}
		else{
			document.getElementById("Page").style.marginTop = "auto";
			document.getElementById("Page").style.marginRight = "auto";
			document.getElementById("Page").style.marginBottom = "auto";
			document.getElementById("Page").style.marginLeft = "auto";
		}
	}
	$(function(){
	(function pulse(){
        $('.message .messageContent span').animate({opacity:'1'}, effectTime).delay(200).animate({opacity:'0'}, effectTime, pulse);
    })();
	});

	// jquery slider
	$(function() {
		$( "#VotingSlider" ).slider({
			range: "max",min: 0,max: 150,value: 75,
			slide: function( event, ui ) {
				$( "#VotingValueDisplayContent" ).val( ui.value/10 );
			}
		});
		$( "#VotingValueDisplayContent" ).val( $( "#VotingSlider" ).slider( "value" )/10 );	
	});

//
	function votingValue() {
		var newVotingValue = newVotingValue.substring(0, 4);
		opacityFactor = newVotingValue / 12; // go to full opacity at positive or negative 0.8 out of 1 vote.
		$("#VotingValueDisplayContent").html(newVotingValue);
		$("#PleaseVoteLowImage").css('opacity', (1.25 - opacityFactor));
		$("#PleaseVoteHighImage").css('opacity', opacityFactor);
	}

// 360 degree rotation in 3secs
//	function AnimateRotate(angle) {
//		var $elem = $('#PleaseWaitImage');
//		$({deg: 0}).animate({deg: angle}, {
//    		duration: 3000,
//    		step: function(now) {
//        		$elem.css({
//            		transform: 'rotate(' + now + 'deg)'
//        		});
//    		}
//		});
//	}

// Fadein fadeout in 3secs
//	function AnimateThanks(opacity) {
//		var $elem = $('#ThanksMessage');
//		$({val: 0}).animate({val: opacity}, {
//    		duration: 3000,
//    		step: function(now) {
//        		$elem.css({
//            		transform: 'opacity(' + now + 'val)'
//        		});
//    		}
//		});
//	}

// Directives
