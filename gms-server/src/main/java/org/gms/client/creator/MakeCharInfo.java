package org.gms.client.creator;

import org.gms.client.Character;
import org.gms.client.Job;
import org.gms.client.inventory.InventoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataTool;

import java.util.HashSet;
import java.util.Set;

/**
 * 创建角色信息数据类，存储角色名、脸型、发型、肤色等创建参数。
 */
public class MakeCharInfo {
    private static final Logger log = LoggerFactory.getLogger(MakeCharInfo.class);
    private static final String FACE_ID = "0";
    private static final String HAIR_ID = "1";
    private static final String HAIR_COLOR_ID = "2";
    private static final String SKIN_ID = "3";
    private static final String TOP_ID = "4";
    private static final String BOTTOM_ID = "5";
    private static final String SHOE_ID = "6";
    private static final String WEAPON_ID = "7";

    private final Set<Integer> charFaces = new HashSet<>();
    private final Set<Integer> charHairs = new HashSet<>();
    private final Set<Integer> charHairColors = new HashSet<>();
    private final Set<Integer> charSkins = new HashSet<>();
    private final Set<Integer> charTops = new HashSet<>();
    private final Set<Integer> charBottoms = new HashSet<>();
    private final Set<Integer> charShoes = new HashSet<>();
    private final Set<Integer> charWeapons = new HashSet<>();

    /**
     * MakeChar信息
     * @param charInfoData charInfoData
     */
    public MakeCharInfo(Data charInfoData) {
        for (Data data : charInfoData.getChildren()) {
            switch (data.getName()) {
                case FACE_ID -> {
                    for (Data faceData : data) {
                        charFaces.add(DataTool.getInt(faceData));
                    }
                }
                case HAIR_ID -> {
                    for (Data hairData : data) {
                        charHairs.add(DataTool.getInt(hairData));
                    }
                }
                case HAIR_COLOR_ID -> {
                    for (Data hairColorData : data) {
                        charHairColors.add(DataTool.getInt(hairColorData));
                    }
                }
                case SKIN_ID -> {
                    for (Data skinData : data) {
                        charSkins.add(DataTool.getInt(skinData));
                    }
                }
                case TOP_ID -> {
                    for (Data topData : data) {
                        charTops.add(DataTool.getInt(topData));
                    }
                }
                case BOTTOM_ID -> {
                    for (Data bottomData : data) {
                        charBottoms.add(DataTool.getInt(bottomData));
                    }
                }
                case SHOE_ID -> {
                    for (Data shoeData : data) {
                        charShoes.add(DataTool.getInt(shoeData));
                    }
                }
                case WEAPON_ID -> {
                    for (Data weaponData : data) {
                        charWeapons.add(DataTool.getInt(weaponData));
                    }
                }
                default -> log.error("Unhandled node inside MakeCharInfo.img.xml: '" + data.getName() + "'");
            }
        }
    }

    /**
     * verifyFaceID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyFaceId(int id) {
        return this.charFaces.contains(id);
    }

    /**
     * verifyHairID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyHairId(int id) {
        if (id % 10 != 0) {
            return this.charHairs.contains(id - (id % 10));
        }
        return this.charHairs.contains(id);
    }

    /**
     * verifyHairColorID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyHairColorId(int id) {
        return this.charHairColors.contains(id % 10);
    }

    /**
     * verifySkinID
     * @param id ID
     * @return 返回值
     */
    public boolean verifySkinId(int id) {
        return this.charSkins.contains(id);
    }

    /**
     * verifyTopID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyTopId(int id) {
        return this.charTops.contains(id);
    }

    /**
     * verifyBottomID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyBottomId(int id) {
        return this.charBottoms.contains(id);
    }

    /**
     * verifyShoeID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyShoeId(int id) {
        return this.charShoes.contains(id);
    }

    /**
     * verifyWeaponID
     * @param id ID
     * @return 返回值
     */
    public boolean verifyWeaponId(int id) {
        return this.charWeapons.contains(id);
    }

    /**
     * verify角色
     * @param character character
     * @return 返回值
     */
    public boolean verifyCharacter(Character character) {
        if (!verifyFaceId(character.getFace())) return false;
        if (!verifyHairId(character.getHair())) return false;
        if (!verifyHairColorId(character.getHair())) return false;
        if (!verifySkinId(character.getSkinColor().getId())) return false;

        // Here we only verify the equipment if the character that's being created is of type 'Beginner'
        // This is because when the Maple Life A or Maple Life B items are used, the client does not send any data
        // regarding what equipment the character should be wearing (as it's all handled server-side)
        Job characterJob = character.getJob();
        if (characterJob == Job.BEGINNER || characterJob == Job.NOBLESSE || characterJob == Job.LEGEND) {
            if (!verifyTopId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -5).getItemId()))
                return false;
            if (!verifyBottomId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -6).getItemId()))
                return false;
            if (!verifyShoeId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -7).getItemId()))
                return false;
            if (!verifyWeaponId(character.getInventory(InventoryType.EQUIPPED).getItem((short) -11).getItemId()))
                return false;
        }

        return true;
    }
}
