package io.dataease.system.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.system.dao.auto.entity.CoreSysSetting;
import io.dataease.system.dao.auto.mapper.CoreSysSettingMapper;
import io.dataease.utils.IDUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysParameterManage {

    private static final String mapKey = "map.key";

    @Resource
    private CoreSysSettingMapper coreSysSettingMapper;

    public String singleVal(String key) {
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pkey", key);
        CoreSysSetting sysSetting = coreSysSettingMapper.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(sysSetting)) {
            return sysSetting.getPval();
        }
        return null;
    }

    public String queryOnlineMap() {
        return singleVal(mapKey);
    }

    public void saveOnlineMap(String val) {

        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pkey", mapKey);
        CoreSysSetting sysSetting = coreSysSettingMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(sysSetting)) {
            sysSetting = new CoreSysSetting();
            sysSetting.setId(IDUtils.snowID());
            sysSetting.setPkey(mapKey);
            sysSetting.setPval(val);
            sysSetting.setType("text");
            sysSetting.setSort(1);
            coreSysSettingMapper.insert(sysSetting);
        }
        sysSetting.setPval(val);
        coreSysSettingMapper.updateById(sysSetting);
    }
    void save(List<CoreSysSetting> boList) {
        List<CoreSysSetting> all = all();
    }

    private List<CoreSysSetting> all() {
        QueryWrapper<CoreSysSetting> queryWrapper = new QueryWrapper<>();
        return coreSysSettingMapper.selectList(queryWrapper);
    }
}
