package org.dragonli.service.general.other;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.dragonli.service.general.interfaces.general.AuthDto;
import org.dragonli.service.general.interfaces.general.AuthService;
import org.dragonli.tools.general.EncryptionUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service(interfaceClass = AuthService.class, register = true, timeout = 150000000, retries = 0, delay = -1)
public class AuthServiceImpl implements AuthService {

    @Value("${service.micro-service.simple-other-service.authPrivatekey:asdf1234}")
    protected String defaultPrivateKey;

    @Value("${service.micro-service.simple-other-service.authTimeout:315360000000}")
    protected Long defaultAuthTimeout;

    @Override
    public Map<String,Object> validateAndRefresh(Map<String, Object> authDto, Boolean refreshTime, Boolean autoGenerate) {
        AuthDto dto = JSON.parseObject(JSON.toJSONString(authDto),AuthDto.class);
        return validateAndRefresh( dto, refreshTime, autoGenerate);
    }

    @Override
    public Map<String,Object> validateAndRefresh(AuthDto authDto, Boolean refreshTime, Boolean autoGenerate) {
        return validateAndRefresh( authDto, refreshTime, autoGenerate, defaultPrivateKey,defaultAuthTimeout);
    }

    @Override
    public Map<String,Object> validateAndRefresh(Map<String, Object> authDto, Boolean refreshTime, Boolean autoGenerate,
            String privateKey,Long timeout) {
        AuthDto dto = JSON.parseObject(JSON.toJSONString(authDto),AuthDto.class);
        return validateAndRefresh( dto, refreshTime, autoGenerate,privateKey,timeout);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String,Object> validateAndRefresh(AuthDto authDto, Boolean refreshTime, Boolean autoGenerate, String privateKey,Long timeout) {
        if(authDto.getUniqueId() == null || authDto.getUid()==null||authDto.getUid()<0
               || authDto.getTime() == null ||authDto.getSign()==null) return null;
        privateKey = privateKey != null ? privateKey : defaultPrivateKey;
        timeout = timeout != null ? timeout : defaultAuthTimeout;
        String rightSign = generateSign(authDto.getUniqueId(),authDto.getUid(),authDto.getCode(),authDto.getTime(),privateKey);
        long now = System.currentTimeMillis();
        boolean validate = authDto.getSign().equals(rightSign);
        boolean hadTimeout = Math.abs(now-authDto.getTime()) > timeout;
        if( validate && !hadTimeout ) return JSON.parseObject(JSON.toJSONString(authDto), HashMap.class);// validate passed
        if( validate && hadTimeout && refreshTime ){// let it passed
            rightSign = generateSign(authDto.getUniqueId(),authDto.getUid(),authDto.getCode(),now,privateKey);
            authDto.setSign(rightSign);
            authDto.setTime(now);
            return JSON.parseObject(JSON.toJSONString(authDto),HashMap.class);
        }
        //now , cant be passed
        if( autoGenerate ) return generate(null,0L,"",privateKey);
        return null;

    }

    @Override
    public Map<String,Object> generate(String uniqueId, Long uid,String code) {
        return generate( uniqueId,  uid, code, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String,Object> generate(String uniqueId, Long uid,String code, String privateKey) {
        code = code == null ? "" : code;
        privateKey = privateKey == null ? defaultPrivateKey : privateKey;
        uniqueId = uniqueId == null || "".equals(uniqueId) ? generateUniqueId(uid) : uniqueId;
        long now = System.currentTimeMillis();
        AuthDto authDto = new AuthDto();
        authDto.setUniqueId(uniqueId);
        authDto.setUid(uid);
        authDto.setTime(now);
        authDto.setCode(code);
        String sign = generateSign(authDto.getUniqueId(),authDto.getUid(),authDto.getCode(),now,privateKey);
        authDto.setSign(sign);
        return JSON.parseObject(JSON.toJSONString(authDto),HashMap.class);
    }

    @Override
    public String generateUniqueId(Long uid) {
        return uid != null && uid > 0 ? "U-" + uid : "G-"+ UUID.randomUUID();
    }

    @Override
    public String signOrigin (String uniqueId,Long uid,String code,Long time){
        return signOrigin(uniqueId,uid,code,time,null);
    }

    @Override
    public String signOrigin (String uniqueId,Long uid,String code,Long time,String privateKey){
        privateKey = privateKey != null ? privateKey : defaultPrivateKey;
        return StringUtils.join(new String[]{uniqueId,uid.toString(),code,time.toString(),privateKey},'|');
    }

    protected String generateSign(String uniqueId,Long uid,String code,Long time,String privateKey){
        try{
            return EncryptionUtil.sha1(signOrigin(uniqueId,uid,code,time,privateKey));
        }catch (Exception e){
            return "";
        }
    }

//    EncryptionUtil

    /*
    @Override
    public Boolean validate(Map<String,Object> authDto)
    {
        return validate(authDto,null);
    }

    @Override
    public Boolean validate(Map<String,Object> authDto, String privateKey)
    {
//        logger.info("test233 a:{}|||{}",authDto.getKey(),privateKey);
        //todo
        try {
            return null != authDto.get("uniqueId") && null != authDto.get("key")
                    && authDto.get("key").equals(
                            generate(authDto, false, false,privateKey).get("rKey"));
        }catch (Exception e){return false;}
    }

    @Override
    public Map<String,Object> generate(Map<String,Object> authDto,Boolean refreshTime,Boolean updateKey) throws Exception {
        return generate(authDto, refreshTime, updateKey,null);
    }

    @Override
    public Map<String,Object> generate(Map<String,Object> authInfo,Boolean refreshTime,Boolean updateKey,String privateKey) throws Exception
    {
        if(authInfo==null)authInfo=generateEmptyAuthDto();
        JSONObject authDto = new JSONObject(authInfo);
        Map<String,Object> result = new HashMap<>();
        if(privateKey == null)privateKey = defaultPrivateKey;
        if( null == authDto.getString("uniqueId") ) authInfo.put("uniqueId"
                ,generateUniqueId(authDto.getLong("eid"),authDto.getLong("uid")));
        if( refreshTime ) authInfo.put("time",System.currentTimeMillis());
        String key = authDto.getString("uniqueId")+"|"+authDto.getLong("eid")+"|"+authDto.getLong("uid")
                +"|"+authDto.getLong("time")+"|"+privateKey;
//        logger.info("test233 b:{}",key);
        String rKey = otherService.sha1(key);
        if(updateKey)authInfo.put("key",rKey);
        result.put("authDto",authDto);
        result.put("rKey",rKey);
        return result;
    }

    @Override
    public String generateUniqueId(Long eid,Long uid)
    {
        if( 0 != eid )
            return "E-"+eid;
        if( 0 != uid )
            return "U-"+uid;
        return "G-"+ UUID.randomUUID();
    }

    public Map<String,Object> generateEmptyAuthDto(){
        Map<String,Object> auth = new HashMap<>();
        auth.put("uniqueId",generateUniqueId(0L,0L));
        auth.put("uid",0L);
        auth.put("eid",0L);
        auth.put("time",System.currentTimeMillis());
        return auth;
    }
*/


}
