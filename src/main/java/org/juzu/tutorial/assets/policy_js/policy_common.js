/*ªÒ»°urlµÿ÷∑*/
function get_url_base()
{
	var local_url = document.location.href;
	
	var get_index=local_url.indexOf("=");
	if(get_index !=-1)
	{
		local_url=local_url.substring(0,get_index);
	}
	
	var dir_index=local_url.lastIndexOf("/");
	if(dir_index !=-1)
	{
		local_url=local_url.substring(0,dir_index);
	}
	
	return local_url;
}
