package org.dragonli.service.general.other;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import org.dragonli.service.general.interfaces.general.IDService;

@Deprecated
//@Service(interfaceClass=IDService.class, register = true, timeout = 15000, retries = 0, delay = -1)
public class IDServiceImpl implements IDService{
	
	//from config
	@Value("${spring.other-server.config.characterConfig}")
	String characterConfig;
	@Value("${spring.othexxr-server.config.encryptConfig}")
	String encryptConfig;

	
	//need init
	protected String[] characterArr;
	protected int[][][] encryptTable;
	protected int encryptRadix;
	protected int encryptCodeLength;
	
	
	@PostConstruct
	public void initCodeNum() {
		initCodeNum(characterConfig, encryptConfig);
	}

	public void initCodeNum(String characterConfig, String encryptConfig)
	{
		characterArr = characterConfig.split(",");
		String[] encryptConfigArr1 = encryptConfig.split(";");
		encryptTable = new int[encryptConfigArr1.length][][];
		for(int i1 = 0 ; i1 < encryptConfigArr1.length ; i1++ )
		{
			String[] encryptConfigArr2 = encryptConfigArr1[i1].split(":");
			int[][] encryptTable2 = encryptTable[i1] = new int[encryptConfigArr2.length][];
			for( int i2 = 0 ; i2 < encryptConfigArr2.length ; i2++ )
			{
				String[] encryptConfigArr3 = encryptConfigArr2[i2].split(",");
				int[] encryptTable3 = encryptTable2[i2] = new int[encryptConfigArr3.length];
				for(int i3=0;i3<encryptTable3.length;i3++)
					encryptTable3[i3] = Integer.parseInt(encryptConfigArr3[i3]);
			}
		}
		encryptRadix = encryptTable.length;
		encryptCodeLength = encryptTable[0].length;
	}
	
	
	@Override
	public String encryptNumberCode(int num)
	{
		return encryptNumberCode(num,encryptCodeLength);
	}
	
	@Override
	public String encryptNumberCode(int num,int encryptCodeLength)
	{
		String[] code = new String[encryptCodeLength];
		int first = 0;
		for( int i = 0 ; i < code.length ; i++ )
		{
			int n1 = (int) Math.pow(encryptRadix,i);
			int n2 = (int) Math.pow(encryptRadix,i+1);
			int index = ( num % n2 ) / n1;
			if( i == 0 ) first = index;
			code[ encryptCodeLength - i - 1 ] = characterArr[ encryptTable[first][i][index] ];
		}
		return Arrays.stream(code).collect(Collectors.joining(""));
	}
}
