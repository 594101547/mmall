<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gbk" />
</head>

<body>
<h2>Hello World!</h2>
<!-- enctype一定要添加 enctype 属性规定在发送到服务器之前应该如何对表单数据进行编码!-->
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="SpringMVC上传文件" />

</form>

<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本上传文件" />

</form>


</body>
</html>
