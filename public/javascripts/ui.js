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

	(function($) {
		$.fn.blinky = function() {
  			var that = this;
    		function go() {
      			$(that).fadeOut().fadeIn();
       			setTimeout(go, 900); 
    		};
    		go();
		};
		})(jQuery);

	function votedCocktail(votedCocktailNumber) {
		votedCocktailNumber = document.getElementById("OrderedCocktailNumber").innerHTML;
	}
	
	var systemStatus = 5;
	function mixingHOOK() {
		status = systemStatus; // implement insertion of system status value 1 for SUCCESS, 0 for CUP_REMOVED, -1 for ERROR
		if (status == 1){
			window.location.href = "#taste";
		}
		else if (status == 0){
			window.location.href = "#vote";
			systemStatus = 5;
		}
		else if (status == -1){
			window.location.href = "#error"; // implement error page
		}
		else{
			systemStatus--;
			setTimeout("mixingHOOK()", 1000)
		}
	}

	function voteHOOK(voteValue) {
		//implement passing of cocktail number & voting value to system
		//cocktailNumber = votedCocktail();
		//var cocktailNumberAndVoteToSystem = [cocktailNumber, voteValue];
	}
	
	function votingValue(newVotingValue) {
		newVotingValue = newVotingValue.substring(0, 4);
		opacityFactor = newVotingValue / 12; // go to full opacity at positive or negative 0.8 out of 1 vote.
		document.getElementById("VotingValueDisplay").innerHTML = newVotingValue;
		document.getElementById("PleaseVoteLowImage").style.opacity = (1.25 - opacityFactor);
		document.getElementById("PleaseVoteHighImage").style.opacity = opacityFactor;
	}

	var count = 3;
	function redirectToOrder(){  
		url = "#order";
		if (count <=0){
			count = 3;
			window.location.href = url;
		}
		else{
			count--;
			//document.getElementById("CountDown").innerHTML = count;
			setTimeout("redirectToOrder()", 1000)
		}
	}
	
	function tasteTime(){
		if (systemStatus == 0){
			mixingHOOK();
		}
		else{
			systemStatus++;
		}
		setTimeout("tasteTime()", 3000)	
	}

	var cocktailNames = ["AVIATION", "BARE AND SHEETS", "BLACK BIRD", "BROWN RUSSIAN", "CUBA LIBRE", "DIRTY DAIQUIRY", "DIRTY MARTINI", "KAMIKAZE", "LIME DROP MARTINI", "SIDECAR", "TSCHUNK", "VESPER", "WHITE BACARDI", "WHITE LADY"];

	var cocktailIngredients = [
	"Gin, Clubmate, Lime juice",
	"Rum, Gin, Triple Sec, Lime juice",
	"Rum, Cola, Triple Sec, Lime juice",
	"Vodka, Clubmate",
	"Rum, Cola, Lime juice",
	"Rum, Lime juice, Cola",
	"Gin, Triple Sec",
	"Vodka, Triple Sec, Lime juice",
	"Vodka, Triple Sec, Lime juice",
	"Gin, Triple Sec, Lime juice",
	"Rum, Clubmate, Lime juice, Brown sugar",
	"Gin, Vodka, Triple Sec",
	"Rum, Lime juice",
	"Gin, Triple Sec, Lime juice",
	];
	
	function readAndSetStep(step){
    	step = (window.location.hash).replace('#', '');
    	if (step.length !== 0) {
        	if (step == "order") {
				document.getElementById("Step1").style.display = "block";
				document.getElementById("Step1").style.visibility = "visible"; //document.getElementById("Step1").style.zIndex = "0";
				$("#Step1").animate({opacity:'1'}, 500);
				document.getElementById("Step1").style.opacity = "1";
				document.getElementById("Step2").style.display = "none";
				document.getElementById("Step2").style.opacity = "0";
				document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
				//
				document.getElementById("OrderedCocktailNumber").innerHTML = 0;
			}
			else if ((step > 0) && (step < 15)){
				for (var i=1; i<15; i++){
					if (step == i){
						document.getElementById("OrderedCocktailNumber").innerHTML = i;
						document.getElementById("ChosenCocktail").innerHTML = cocktailNames[i-1];
						document.getElementById("CocktailIngredients").innerHTML = "<br />"+cocktailIngredients[i-1];
					}
				}
				document.getElementById("Step1").style.display = "none";
				document.getElementById("Step1").style.opacity = "0";
				document.getElementById("Step1").style.visibility = "hidden"; //document.getElementById("Step1").style.zIndex = "-1";
				document.getElementById("Step2").style.display = "block";
				document.getElementById("Step2").style.visibility = "visible"; //document.getElementById("Step2").style.zIndex = "0";
				$("#Step2").animate({opacity:'1'}, 500);
				document.getElementById("Step2").style.opacity = "1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
				//
				$('#Page').scrollTop(0);
				document.getElementById("SystemMessage").innerHTML = "Your order is";
				document.getElementById("ChosenCocktail").style.display = "block";
				document.getElementById("StartMixing").style.display = "block";
				document.getElementById("CancelMixing").style.display = "block";
				document.getElementById("StopMixing").style.display = "none";
				document.getElementById("PleaseWaitImage").style.display = "none";
				document.getElementById("PleaseWaitImage").style.opacity = 0;
				document.getElementById("CocktailIngredients").style.display = "block";
			}
			else if (step == "mix"){
				document.getElementById("Step1").style.display = "none";
				document.getElementById("Step1").style.opacity = "0";
				document.getElementById("Step1").style.visibility = "hidden"; //document.getElementById("Step1").style.zIndex = "-1";
				document.getElementById("Step2").style.display = "block";
				document.getElementById("Step2").style.visibility = "visible"; //document.getElementById("Step2").style.zIndex = "0";
				$("#Step2").animate({opacity:'1'}, 500);
				document.getElementById("Step2").style.opacity = "1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
				//
				document.getElementById("StartMixing").style.display = "none";
				document.getElementById("CancelMixing").style.display = "none";
				$('#ChosenCocktail').animate({position: 'absolute'});
				//document.getElementById("StopMixing").style.display = "block";
				$('#Page').scrollTop(0);
				document.getElementById("SystemMessage").innerHTML = "Preparing cocktail";
				document.getElementById("PleaseWaitImage").style.display = "block";
				document.getElementById("CocktailIngredients").style.display = "none";
				$("#PleaseWaitImage").animate({opacity:1}, 0);
				AnimateRotate(360);
				systemStatus = 5;
				mixingHOOK();
			}
			else if (step == "taste"){
				document.getElementById("Step1").style.display = "none";
				document.getElementById("Step1").style.opacity = "0";
				document.getElementById("Step1").style.visibility = "hidden"; //document.getElementById("Step1").style.zIndex = "-1";
				document.getElementById("Step2").style.display = "none";
				document.getElementById("Step2").style.opacity = "0";
				document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
				document.getElementById("Step3").style.display = "block";
				document.getElementById("Step3").style.visibility = "visible"; //document.getElementById("Step3").style.zIndex = "0";
				$("#Step3").animate({opacity:'1'}, 500);
				document.getElementById("Step3").style.opacity = "1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
				//
				$('#Page').scrollTop(0);
				systemStatus = -2;
				tasteTime();
			}
			else if (step == "vote"){
				document.getElementById("Step1").style.display = "none";
				document.getElementById("Step1").style.opacity = "0";
				document.getElementById("Step1").style.visibility = "hidden"; //document.getElementById("Step1").style.zIndex = "-1";
				document.getElementById("Step2").style.display = "none";
				document.getElementById("Step2").style.opacity = "0";
				document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "block";
				document.getElementById("Step4").style.visibility = "visible"; //document.getElementById("Step4").style.zIndex = "0";
				$("#Step4").animate({opacity:'1'}, 500);
				document.getElementById("Step4").style.opacity = "1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
				//
				$( "#VotingValueDisplayContent" ).value = 7.5; //document.getElementById("VotingSliderInput").value = 7.5;
				//votingValue($( "#VotingValueDisplayContent" ).value); //votingValue(document.getElementById("VotingSliderInput").value);
				$("#VotingSliderInput").animate({value: 7.5}, 10);
				$('#Page').scrollTop(0);
			}
			else if (step == "thankyou"){
				document.getElementById("Step1").style.display = "none";
				document.getElementById("Step1").style.opacity = "0";
				document.getElementById("Step1").style.visibility = "hidden"; //document.getElementById("Step1").style.zIndex = "-1";
				document.getElementById("Step2").style.display = "none";
				document.getElementById("Step2").style.opacity = "0";
				document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "block";
				document.getElementById("Step5").style.visibility = "visible"; //document.getElementById("Step5").style.zIndex = "0";
				$("#Step5").animate({opacity:'1'}, 500);
				document.getElementById("Step5").style.opacity = "1";
				//
				$('#Page').scrollTop(0);
				document.getElementById("Thankyou").style.opacity = "1";
				$("#Thankyou").animate({opacity: 0}, 3000);	
				redirectToOrder();
			}
			else {
				document.getElementById("OrderedCocktailNumber").innerHTML = 0;
				//document.getElementById("Step1").style.overflowY = "scroll";
				document.getElementById("Step1").style.display = "block";
				$("#Step1").animate({opacity:'1'}, 500);
				document.getElementById("Step1").style.opacity = "1";
				document.getElementById("Step1").style.visibility = "visible"; //document.getElementById("Step1").style.zIndex = "0";
				document.getElementById("Step2").style.display = "none";
				document.getElementById("Step2").style.opacity = "0";
				document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
				document.getElementById("Step3").style.display = "none";
				document.getElementById("Step3").style.opacity = "0";
				document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
				document.getElementById("Step4").style.display = "none";
				document.getElementById("Step4").style.opacity = "0";
				document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
				document.getElementById("Step5").style.display = "none";
				document.getElementById("Step5").style.opacity = "0";
				document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
			}
    	}
		else if (step.length == 0){
			document.getElementById("OrderedCocktailNumber").innerHTML = 0;
			//document.getElementById("Step1").style.overflowY = "scroll";
			document.getElementById("Step1").style.display = "block";
			$("#Step1").animate({opacity:'1'}, 500);
			document.getElementById("Step1").style.opacity = "1";
			document.getElementById("Step1").style.visibility = "visible"; //document.getElementById("Step1").style.zIndex = "0";
			document.getElementById("Step2").style.display = "none";
			document.getElementById("Step2").style.opacity = "0";
			document.getElementById("Step2").style.visibility = "hidden"; //document.getElementById("Step2").style.zIndex = "-1";
			document.getElementById("Step3").style.display = "none";
			document.getElementById("Step3").style.opacity = "0";
			document.getElementById("Step3").style.visibility = "hidden"; //document.getElementById("Step3").style.zIndex = "-1";
			document.getElementById("Step4").style.display = "none";
			document.getElementById("Step4").style.opacity = "0";
			document.getElementById("Step4").style.visibility = "hidden"; //document.getElementById("Step4").style.zIndex = "-1";
			document.getElementById("Step5").style.display = "none";
			document.getElementById("Step5").style.opacity = "0";
			document.getElementById("Step5").style.visibility = "hidden"; //document.getElementById("Step5").style.zIndex = "-1";
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
			//document.getElementById("Page").style.marginTop = "0";
			//document.getElementById("Page").style.marginRight = "0";
			//document.getElementById("Page").style.marginBottom = "0";
			//document.getElementById("Page").style.marginLeft = "0";
		}
		else{
			document.getElementById("Page").style.marginTop = "auto";
			document.getElementById("Page").style.marginRight = "auto";
			document.getElementById("Page").style.marginBottom = "auto";
			document.getElementById("Page").style.marginLeft = "auto";
		}
	}
	$("#ChosenCocktail").blinky();
	$("#PleaseVoteMessage").blinky();
	function gotoURL(url){
		window.location.href = "#"+url;
	}
	$(function() {
		$( "#VotingSlider" ).slider({
			range: "max",min: 0,max: 150,value: 75,
			slide: function( event, ui ) {
				$( "#VotingValueDisplayContent" ).val( ui.value/10 );
			}
		});
		$( "#VotingValueDisplayContent" ).val( $( "#VotingSlider" ).slider( "value" )/10 );	
	});