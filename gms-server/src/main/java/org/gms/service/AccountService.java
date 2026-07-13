package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.DefaultDates;
import org.gms.config.GameConfig;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.model.dto.AddAccountDTO;
import org.gms.model.dto.UpdateAccountByGmDTO;
import org.gms.model.dto.UpdateAccountByUserDTO;
import org.gms.net.server.Server;
import org.gms.util.BCrypt;
import org.gms.util.HexTool;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

import static org.gms.client.Client.LOGIN_LOGGEDIN;
import static org.gms.client.Client.LOGIN_NOTLOGGEDIN;
import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.IpbansDOTableDef.IPBANS_D_O;

/**
 * 账号服务类
 * 提供游戏账号的CRUD操作、密码加密验证、账号封禁/解封、登录状态管理等功能
 */
@Service
@AllArgsConstructor
public class AccountService {
    private final AccountsMapper accountsMapper;
    private final CharactersMapper charactersMapper;
    private final IpbansMapper ipbansMapper;
    private final MacbansMapper macbansMapper;
    private final QuickslotkeymappedMapper quickslotkeymappedMapper;

    /**
     * 根据账号名称查找账号
     * @param name 账号名称
     * @return 账号实体对象
     */
    public AccountsDO findByName(String name) {
        return accountsMapper.selectOneByName(name);
    }

    /**
     * 根据账号ID查找账号
     * @param id 账号ID
     * @return 账号实体对象
     */
    public AccountsDO findById(int id) {
        return accountsMapper.selectOneById(id);
    }

    /**
     * 获取当前登录用户信息
     * 从Spring Security上下文中获取当前认证的用户
     * @return 当前登录的账号实体对象
     */
    public AccountsDO getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findByName(userDetails.getUsername());
    }

    /**
     * 分页查询账号列表
     * @param page 页码
     * @param size 每页条数
     * @param id 账号ID（精确匹配）
     * @param name 账号名称（模糊匹配）
     * @param lastLoginStart 最后登录开始时间
     * @param lastLoginEnd 最后登录结束时间
     * @param createdAtStart 创建时间开始
     * @param createdAtEnd 创建时间结束
     * @return 分页账号结果
     */
    public Page<AccountsDO> getAccountList(Integer page,
                                           Integer size,
                                           Integer id,
                                           String name,
                                           String lastLoginStart,
                                           String lastLoginEnd,
                                           String createdAtStart,
                                           String createdAtEnd) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (id != null) queryWrapper.eq("id", id);
        if (name != null) queryWrapper.like("name", name);
        if (lastLoginStart != null) queryWrapper.ge(AccountsDO::getLastlogin, lastLoginStart);
        if (lastLoginEnd != null) queryWrapper.le(AccountsDO::getLastlogin, lastLoginEnd);
        if (createdAtStart != null) queryWrapper.ge(AccountsDO::getCreatedat, createdAtStart);
        if (createdAtEnd != null) queryWrapper.le(AccountsDO::getCreatedat, createdAtEnd);

        if (page == null) page = 1;
        if (size == null) size = Integer.MAX_VALUE;
        return accountsMapper.paginateWithRelations(page, size, queryWrapper);
    }

    /**
     * 更新账号信息
     * @param condition 包含更新字段的账号对象
     */
    public void update(AccountsDO condition) {
        accountsMapper.update(condition);
    }

    /**
     * 创建新账号
     * @param submitData 新账号信息
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    public void addAccount(AddAccountDTO submitData) throws NoSuchAlgorithmException {
        // 防止swagger调用，后续的语言路由都受影响
        RequireUtil.requireNotNull(submitData.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        RequireUtil.requireNull(findByName(submitData.getName()), I18nUtil.getExceptionMessage("AccountService.addAccount.exception1"));
        AccountsDO account = AccountsDO.builder()
                .name(submitData.getName())
                .password(encryptPassword(submitData.getPassword()))
                .birthday(submitData.getBirthday())
                .tempban(Timestamp.valueOf(DefaultDates.getTempban()))
                .language(submitData.getLanguage())
                .lastlogin(Timestamp.valueOf(DefaultDates.getTempban()))
                .build();
        // 可以直接用insertSelective忽略null值
        accountsMapper.insertSelective(account);
    }

    /**
     * 用户自己更新账号信息
     * 需要验证旧密码，只能修改部分字段
     * @param submitData 用户提交的更新信息
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    public void updateAccountByUser(UpdateAccountByUserDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = getCurrentUser();
        RequireUtil.requireTrue(checkPassword(submitData.getOldPwd(), account), I18nUtil.getExceptionMessage("AccountService.updateAccountByUser.oldPassword"));
        // 防止swagger调用，后续的语言路由都受影响
        RequireUtil.requireNotNull(submitData.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));

        AccountsDO newData = new AccountsDO();
        newData.setId(account.getId());
        if (submitData.getNewPwd() != null && submitData.getNewPwd().length() >= 6) {
            newData.setPassword(encryptPassword(submitData.getNewPwd()));
        }
        newData.setPin(submitData.getPin());
        newData.setPic(submitData.getPic());
        newData.setBirthday(submitData.getBirthday());
        newData.setNick(submitData.getNick());
        newData.setEmail(submitData.getEmail());
        newData.setLanguage(submitData.getLanguage());

        accountsMapper.update(newData);
    }

    /**
     * GM更新账号信息
     * 可以修改所有账号字段，包括点券、权限等级等
     * @param id 要修改的账号ID
     * @param submitData GM提交的更新信息
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    public void updateAccountByGM(int id, UpdateAccountByGmDTO submitData) throws NoSuchAlgorithmException {
        AccountsDO account = findById(id);
        RequireUtil.requireNotNull(account, I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        // 防止swagger调用，后续的语言路由都受影响
        RequireUtil.requireNotNull(account.getLanguage(), I18nUtil.getExceptionMessage("LANGUAGE_NOT_SUPPORT"));
        RequireUtil.requireFalse(account.getLoggedin() == LOGIN_LOGGEDIN, I18nUtil.getExceptionMessage("AccountService.isOnline"));
        if (submitData.getNewPwd() != null && submitData.getNewPwd().length() >= 6) {
            account.setPassword(encryptPassword(submitData.getNewPwd()));
        }
        account.setPin(submitData.getPin());
        account.setPic(submitData.getPic());
        account.setBirthday(submitData.getBirthday());
        account.setNxCredit(submitData.getNxCredit());
        account.setMaplePoint(submitData.getMaplePoint());
        account.setNxPrepaid(submitData.getNxPrepaid());
        account.setCharacterslots(submitData.getCharacterslots());
        account.setGender(submitData.getGender());
        account.setWebadmin(submitData.getWebadmin());
        account.setNick(submitData.getNick());
        account.setMute(submitData.getMute());
        account.setEmail(submitData.getEmail());
        account.setRewardpoints(submitData.getRewardpoints());
        account.setVotepoints(submitData.getVotepoints());
        account.setLanguage(submitData.getLanguage());

        accountsMapper.update(account);
    }

    /**
     * 加密密码
     * 根据配置选择使用BCrypt或SHA-512加密
     * @param password 原始密码
     * @return 加密后的密码哈希
     * @throws NoSuchAlgorithmException 加密算法不存在时抛出
     */
    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        return GameConfig.getServerBoolean("bcrypt_migration") ? BCrypt.hashpw(password, BCrypt.gensalt(12)) : BCrypt.hashpwSHA512(password);
    }

    /**
     * 验证密码是否正确
     * 支持BCrypt、明文、SHA-1、SHA-512多种格式
     * @param pwd 待验证的密码
     * @param accountsDO 账号实体
     * @return 密码是否正确
     */
    public boolean checkPassword(String pwd, AccountsDO accountsDO) {
        String passHash = accountsDO.getPassword();
        if (passHash.charAt(0) == '$' && passHash.charAt(1) == '2' && BCrypt.checkpw(pwd, passHash)) {
            return true;
        } else {
            return pwd.equals(passHash) || checkHash(passHash, "SHA-1", pwd) || checkHash(passHash, "SHA-512", pwd);
        }
    }

    /**
     * 检查密码哈希是否匹配
     * @param hash 存储的哈希值
     * @param type 哈希算法类型
     * @param password 待验证的密码
     * @return 哈希是否匹配
     */
    private static boolean checkHash(String hash, String type, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance(type);
            digester.update(password.getBytes(StandardCharsets.UTF_8), 0, password.length());
            return HexTool.toHexString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }

    /**
     * 重置指定账号的登录状态
     * @param id 账号ID
     */
    public void resetAllLoggedIn(int id) {
        RequireUtil.requireNotNull(findById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        AccountsDO account = new AccountsDO();
        account.setId(id);
        account.setLoggedin(LOGIN_NOTLOGGEDIN);
        accountsMapper.update(account);
    }

    /**
     * 封禁账号
     * 同时封禁在线角色的MAC、IP，并强制下线
     * @param accountId 账号ID
     * @param reason 封禁原因
     */
    public void banAccount(int accountId, String reason) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        // 封停账号
        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(true);
        account.setBanreason(reason);
        accountsMapper.update(account);
        // 遍历账号下的角色，如果在线，追封客户端/Mac/IP
        List<CharactersDO> characterList = charactersMapper.selectIdAndWorldListByAccountId(accountId); // 仅查询角色ID和所在world
        for (CharactersDO chr : characterList) {
            Character player = Server.getInstance()
                    .getWorlds()
                    .get(chr.getWorld())
                    .getPlayerStorage()
                    .getCharacterById(chr.getId());
            if (player == null) continue; // 角色离线
            player.setBanned(true);
            Client c = player.getClient(); // 角色在线，获取客户端
            c.banMacs(); // 封禁Mac
            // c.banHWID(); // 封禁客户端 操作不可逆？
            // 封禁IP
            String ip = c.getRemoteAddress();
            IpbansDO ipban = IpbansDO.builder().ip(ip).aid(String.valueOf(accountId)).build();
            ipbansMapper.insertSelective(ipban);
            // 强制离线，这个方法只是中断了连接不会造成客户端退出，但是实际跟掉线没什么区别
            c.disconnect(false, false);
        }
    }

    /**
     * 解封账号
     * 同时解封对应的MAC和IP封禁记录
     * @param accountId 账号ID
     */
    public void unbanAccount(int accountId) {
        RequireUtil.requireNotNull(findById(accountId), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));

        // 解封账号
        AccountsDO account = new AccountsDO();
        account.setId(accountId);
        account.setBanned(false);
        accountsMapper.update(account);
        // 解封Mac
        macbansMapper.deleteByQuery(new QueryWrapper().eq(MacbansDO::getAid, accountId));
        // 解封Ip
        ipbansMapper.deleteByQuery(new QueryWrapper().eq(IpbansDO::getAid, accountId));
    }

    /**
     * 重置所有账号的登录状态为未登录
     * 通常在服务器启动时调用
     */
    public void resetAllLoggedIn() {
        accountsMapper.updateAllLoggedIn(0);
    }

    /**
     * 封禁在线角色
     * @param chr 角色对象
     * @param reason 封禁原因
     */
    public void ban(Character chr, String reason) {
        accountsMapper.update(AccountsDO.builder().banned(true).id(chr.getAccountId()).banreason(reason).build());
        // 更新在线的ban状态
        chr.setBanned(true);
    }

    /**
     * 封禁账号或IP
     * @param str 账号名/角色名/IP地址
     * @param reason 封禁原因
     * @param isAccount true表示按账号名封禁，false表示按角色名封禁
     */
    public void ban(String str, String reason, boolean isAccount) {
        if (str.matches("[0-9]{1,3}\\..*")) {
            if (isBanned(str)) {
                return;
            }
            ipbansMapper.insertSelective(IpbansDO.builder().ip(str).build());
            return;
        }
        Integer accountId = null;
        if (isAccount) {
            AccountsDO accountsDO = findByName(str);
            if (accountsDO != null) {
                accountId = accountsDO.getId();
            }
        } else {
            List<CharactersDO> charactersDOS = charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.NAME.eq(str)));
            if (!charactersDOS.isEmpty()) {
                accountId = charactersDOS.getFirst().getAccountid();
            }
        }
        if (accountId == null) {
            throw new NoSuchElementException();
        }
        accountsMapper.update(AccountsDO.builder()
                .id(accountId)
                .banreason(reason)
                .banned(true)
                .build());
    }

    /**
     * 检查IP是否被封禁
     * @param ip IP地址
     * @return IP是否被封禁
     */
    public boolean isBanned(String ip) {
        return ipbansMapper.selectCountByQuery(QueryWrapper.create().where(IPBANS_D_O.IP.eq(ip))) > 0;
    }

    /**
     * 获取账号的快捷键映射配置
     * @param accountId 账号ID
     * @return 快捷键映射实体
     */
    public QuickslotkeymappedDO getQuickSlotKeyMap(int accountId) {
        return quickslotkeymappedMapper.selectOneById(accountId);
    }
}
