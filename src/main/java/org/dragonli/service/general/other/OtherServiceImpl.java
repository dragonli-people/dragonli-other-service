
package org.dragonli.service.general.other;

import com.alibaba.dubbo.config.annotation.Service;
import org.dragonli.service.general.interfaces.general.OtherService;
import org.dragonli.tools.general.EncryptionUtil;

@Service(interfaceClass=OtherService.class, register = true, timeout = 15000, retries = -1, delay = -1)
public class OtherServiceImpl implements OtherService{



	@Override
	public String sha1(String str) throws Exception
	{
		return EncryptionUtil.sha1(str);
	}

	@Override
	public String sha1(String str,Integer len) throws Exception{
		return EncryptionUtil.sha1(str,len);
	}

	@Override
	public String md5(String str) throws Exception{
		return EncryptionUtil.md5(str);
	}

	@Override
	public String md5(String str,Integer len) throws Exception{
		return EncryptionUtil.md5(str,len);
	}


	@Override
	public String byteArrayToHexStr(byte[] arr)
	{
		return EncryptionUtil.byteArrayToHexStr(arr);
	}

	@Override
	public String toShortCryptoCode(String sourceCode,Integer shortCodeLength){
		return EncryptionUtil.toShortCryptoCode(sourceCode,shortCodeLength);
	}

	@Override
	public String subtraction(String str1, String str2,int scale) throws Exception {
		return EncryptionUtil.subtraction(str1,str2,scale);
	}

	@Override
	public String multiplication(String str1, String str2,int scale) throws Exception {
		return EncryptionUtil.multiplication(str1,str2,scale);
	}

	@Override
	public String division(String str1, String str2,int scale) throws Exception {
		return EncryptionUtil.division(str1,str2,scale);
	}

	@Override
	public String addition(String str1, String str2,int scale) throws Exception {
		return EncryptionUtil.addition(str1,str2,scale);
	}

}
