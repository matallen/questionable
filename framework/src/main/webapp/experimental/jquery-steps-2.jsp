<html>
	<head>
		<link   href="http://fonts.googleapis.com/css?family=Open+Sans:400,600" rel="stylesheet" type="text/css">
		<link   href="jquery-steps-2.css" rel="stylesheet" />
		
    <script  type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
    <script  type="text/javascript" src="js/jquery.steps.min.js" type="text/javascript"></script>
        
	</head>
	<body>
	
	<script>
	
		$( document ).ready(function() {
			var form = $("#example-advanced-form").show();
			 
			form.steps({
			    headerTag: "h3",
			    bodyTag: "fieldset",
			    transitionEffect: "slideLeft",
			    onStepChanging: function (event, currentIndex, newIndex)
			    {
			        // Allways allow previous action even if the current form is not valid!
			        if (currentIndex > newIndex)
			        {
			            return true;
			        }
			        // Forbid next action on "Warning" step if the user is to young
			        if (newIndex === 3 && Number($("#age-2").val()) < 18)
			        {
			            return false;
			        }
			        // Needed in some cases if the user went back (clean up)
			        if (currentIndex < newIndex)
			        {
			            // To remove error styles
			            form.find(".body:eq(" + newIndex + ") label.error").remove();
			            form.find(".body:eq(" + newIndex + ") .error").removeClass("error");
			        }
			        form.validate().settings.ignore = ":disabled,:hidden";
			        return form.valid();
			    },
			    onStepChanged: function (event, currentIndex, priorIndex)
			    {
			        // Used to skip the "Warning" step if the user is old enough.
			        if (currentIndex === 2 && Number($("#age-2").val()) >= 18)
			        {
			            form.steps("next");
			        }
			        // Used to skip the "Warning" step if the user is old enough and wants to the previous step.
			        if (currentIndex === 2 && priorIndex === 3)
			        {
			            form.steps("previous");
			        }
			    },
			    onFinishing: function (event, currentIndex)
			    {
			        form.validate().settings.ignore = ":disabled";
			        return form.valid();
			    },
			    onFinished: function (event, currentIndex)
			    {
			        alert("Submitted!");
			    }
			}).validate({
			    errorPlacement: function errorPlacement(error, element) { element.before(error); },
			    rules: {
			        confirm: {
			            equalTo: "#password-2"
			        }
			    }
			});
		});
	</script>
		<section id="advanced-form">
			<h2 class="page-header">Advanced Form Example</h2>
			<p></p>
			<form id="example-advanced-form" action="#" style="" role="application" class="wizard clearfix" novalidate="novalidate">
				<div class="steps clearfix">
					<ul role="tablist">
						<li role="tab" class="first current error" aria-disabled="false" aria-selected="true">
							<a id="example-advanced-form-t-0" href="#example-advanced-form-h-0" aria-controls="example-advanced-form-p-0">
								<span class="current-info audible">current step: </span>
								<span class="number">1.</span>
								Account
							</a>
						</li>
						<li role="tab" class="disabled" aria-disabled="true">
							<a id="example-advanced-form-t-1" href="#example-advanced-form-h-1" aria-controls="example-advanced-form-p-1">
								<span class="number">2.</span>
								Profile
							</a>
						</li>
						<li role="tab" class="disabled" aria-disabled="true">
							<a id="example-advanced-form-t-2" href="#example-advanced-form-h-2" aria-controls="example-advanced-form-p-2">
								<span class="number">3.</span>
								Warning
							</a>
						</li>
						<li role="tab" class="disabled last" aria-disabled="true">
							<a id="example-advanced-form-t-3" href="#example-advanced-form-h-3" aria-controls="example-advanced-form-p-3">
								<span class="number">4.</span>
								Finish
							</a>
						</li>
					</ul>
				</div>
				<div class="content clearfix">
					<h3 id="example-advanced-form-h-0" tabindex="-1" class="title current">Account</h3>
					<fieldset id="example-advanced-form-p-0" role="tabpanel" aria-labelledby="example-advanced-form-h-0" class="body current" aria-hidden="false">
						<legend>Account Information</legend>
	
						<label for="userName-2">User name *</label>
						<label for="userName-2" class="error">This field is required.</label>
						<input id="userName-2" name="userName" type="text" class="required error">
							<label for="password-2">Password *</label>
							<label for="password-2" class="error">This field is required.</label>
							<input id="password-2" name="password" type="text" class="required error">
								<label for="confirm-2">Confirm Password *</label>
								<label for="confirm-2" class="error">This field is required.</label>
								<input id="confirm-2" name="confirm" type="text" class="required error">
									<p>(*) Mandatory</p>
					</fieldset>
	
					<h3 id="example-advanced-form-h-1" tabindex="-1" class="title">Profile</h3>
					<fieldset id="example-advanced-form-p-1" role="tabpanel" aria-labelledby="example-advanced-form-h-1" class="body" aria-hidden="true" style="display: none;">
						<legend>Profile Information</legend>
	
						<label for="name-2">First name *</label>
						<input id="name-2" name="name" type="text" class="required">
							<label for="surname-2">Last name *</label>
							<input id="surname-2" name="surname" type="text" class="required">
								<label for="email-2">Email *</label>
								<input id="email-2" name="email" type="text" class="required email">
									<label for="address-2">Address</label>
									<input id="address-2" name="address" type="text">
										<label for="age-2">Age (The warning step will show up if age is less than 18) *</label>
										<input id="age-2" name="age" type="text" class="required number">
											<p>(*) Mandatory</p>
					</fieldset>
	
					<h3 id="example-advanced-form-h-2" tabindex="-1" class="title">Warning</h3>
					<fieldset id="example-advanced-form-p-2" role="tabpanel" aria-labelledby="example-advanced-form-h-2" class="body" aria-hidden="true" style="display: none;">
						<legend>You are to young</legend>
	
						<p>Please go away ;-)</p>
					</fieldset>
	
					<h3 id="example-advanced-form-h-3" tabindex="-1" class="title">Finish</h3>
					<fieldset id="example-advanced-form-p-3" role="tabpanel" aria-labelledby="example-advanced-form-h-3" class="body" aria-hidden="true" style="display: none;">
						<legend>Terms and Conditions</legend>
	
						<input id="acceptTerms-2" name="acceptTerms" type="checkbox" class="required">
							<label for="acceptTerms-2">I agree with the Terms and Conditions.</label>
					</fieldset>
				</div>
				<div class="actions clearfix">
					<ul role="menu" aria-label="Pagination">
						<li class="disabled" aria-disabled="true">
							<a href="#previous" role="menuitem">Previous</a>
						</li>
						<li aria-hidden="false" aria-disabled="false">
							<a href="#next" role="menuitem">Next</a>
						</li>
						<li aria-hidden="true" style="display: none;">
							<a href="#finish" role="menuitem">Finish</a>
						</li>
					</ul>
				</div>
			</form>
		</section>
	</body>
</html>