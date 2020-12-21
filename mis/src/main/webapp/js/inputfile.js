var content=`<div class="input-append input-group">	
	                                			<span class="input-group-btn ">
	                                				<p class="btn btn-white ">选择文件</p>
	                                			</span>					
	                                			<input class="input-large form-control " type="text ">
	                                		</div>`;
		$(":file").hide().after(content);
		$(".input-append").bind("click",function(){
			$(this).siblings(":file").click();
			$(this).siblings(":file").change(function(){
				var file=$(this).val();
				var position=file.lastIndexOf("\\");
				var fileName=file.substring(position+1);
				$(this).siblings().children().last().val(fileName);
			});
		});