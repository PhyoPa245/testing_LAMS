 <div class="card card-plain" >
	<div class="card-header" id="heading-advanced-questions">
		<span class="card-title collapsable-icon-left">
			<a class=collapsed role="button" data-toggle="collapse" href="#advanced-questions" 
				aria-expanded="true" aria-controls="advanced-questions" >
			<fmt:message key="label.Questions" /></a>
		</span>
     </div>
     
     <div id="advanced-questions" class="card-collapse collapse" role="tabcard" aria-labelledby="heading-advanced-questions">

		<table class="table table-sm table-striped">
			<c:forEach var="question" items="${content.mcQueContents}" varStatus="i">
	
				<tr>			
					<td><b>  <fmt:message key="label.question.only"/>&nbsp;${i.index + 1}:</b> (<fmt:message key="label.mark"/>&nbsp;<c:out value="${question.mark}"/>)<br/>
						<c:out value="${question.question}" escapeXml="false"/>						
					</td>
				</tr>
							
				<tr>					
					<td>  <b> <fmt:message key="label.mc.options.col"/>  </b> 
						<table class="table table-sm" style="margin-bottom:0px">
							<c:forEach var="option" items="${question.mcOptionsContents}" varStatus="optionStatus">
								<tr>			
									<c:if test="${prefixAnswersWithLetters}">
									<td style="vertical-align:top">
										${option.formatPrefixLetter(optionStatus.index)}	
									</td>
									</c:if>	
									<td>
										<c:if test="${option.correctOption}"> 		
											(<fmt:message key="label.correct"/>)
										</c:if>	
										<c:out value="${option.mcQueOptionText}" escapeXml="false"/> 
									</td>	
								</tr>
							</c:forEach>		  	
						</table>
					</td>  
				</tr>			
	
			</c:forEach>		  	
	
		  	<tr>
		  	 	<td class="text-left"> <b> 
		  	 		<fmt:message key="label.passingMark"/>: </b> <c:out value="${passMark}"/> 
		  	 	</td>
		  	</tr>
		</table>

	</div>
</div>		