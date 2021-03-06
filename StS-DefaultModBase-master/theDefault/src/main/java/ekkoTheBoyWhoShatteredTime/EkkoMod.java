package ekkoTheBoyWhoShatteredTime;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.helpers.TooltipInfo;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import ekkoTheBoyWhoShatteredTime.cards.Process;
import ekkoTheBoyWhoShatteredTime.cards.*;
import ekkoTheBoyWhoShatteredTime.characters.EkkoTheBoyWhoShatteredTime;
import ekkoTheBoyWhoShatteredTime.potions.BottledSheen;
import ekkoTheBoyWhoShatteredTime.potions.EquilibriumPotion;
import ekkoTheBoyWhoShatteredTime.potions.ResonatingPotion;
import ekkoTheBoyWhoShatteredTime.relics.*;
import ekkoTheBoyWhoShatteredTime.util.IDCheckDontTouchPls;
import ekkoTheBoyWhoShatteredTime.util.TextureLoader;
import ekkoTheBoyWhoShatteredTime.variables.DefaultCustomVariable;
import ekkoTheBoyWhoShatteredTime.variables.DefaultSecondMagicNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
// Please don't just mass replace "theDefault" with "yourMod" everywhere.
// It'll be a bigger pain for you. You only need to replace it in 3 places.
// I comment those places below, under the place where you set your ID.

//TODO: FIRST THINGS FIRST: RENAME YOUR PACKAGE AND ID NAMES FIRST-THING!!!
// Right click the package (Open the project pane on the left. Folder with black dot on it. The name's at the very top) -> Refactor -> Rename, and name it whatever you wanna call your mod.
// Scroll down in this file. Change the ID from "theDefault:" to "yourModName:" or whatever your heart desires (don't use spaces). Dw, you'll see it.
// In the JSON strings (resources>localization>eng>[all them files] make sure they all go "yourModName:" rather than "theDefault". You can ctrl+R to replace in 1 file, or ctrl+shift+r to mass replace in specific files/directories (Be careful.).
// Start with the DefaultCommon cards - they are the most commented cards since I don't feel it's necessary to put identical comments on every card.
// After you sorta get the hang of how to make cards, check out the card template which will make your life easier

/*
 * With that out of the way:
 * Welcome to this super over-commented Slay the Spire modding base.
 * Use it to make your own mod of any type. - If you want to add any standard in-game content (character,
 * cards, relics), this is a good starting point.
 * It features 1 character with a minimal set of things: 1 card of each type, 1 debuff, couple of relics, etc.
 * If you're new to modding, you basically *need* the BaseMod wiki for whatever you wish to add
 * https://github.com/daviscook477/BaseMod/wiki - work your way through with this base.
 * Feel free to use this in any way you like, of course. MIT licence applies. Happy modding!
 *
 * And pls. Read the comments.
 */

@SpireInitializer
public class EkkoMod implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        OnStartBattleSubscriber,
        PostInitializeSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(EkkoMod.class.getName());
    public static boolean lastTurnAttacked;
    public static Object hpAtTurnStart;
    public static boolean gainedStrDexThisTurn;
    private static String modID;
    public static boolean ResonanceCheck;
    public static int checkEnergy = 0;
    public static int usedEnergy = 0;
    public void receiveOnBattleStart (AbstractRoom room) {
        EkkoMod.lastTurnAttacked = false;
        EkkoMod.gainedStrDexThisTurn = false;
        EkkoMod.hpAtTurnStart = AbstractDungeon.player.currentHealth;
        EkkoMod.ResonanceCheck = false;
        EkkoMod.checkEnergy = 0;
        EkkoMod.usedEnergy = 0;
    }

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties theDefaultDefaultSettings = new Properties();
    public static final String ENABLE_PLACEHOLDER_SETTINGS = "enablePlaceholder";
    public static boolean enablePlaceholder = true; // The boolean we'll be setting on/off (true/false)

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Ekko, the boy who shattered time";
    private static final String AUTHOR = "Diamsword"; // And pretty soon - You!
    private static final String DESCRIPTION = "A mod inspired by ekko from LOL";
    
    // =============== INPUT TEXTURE LOCATION =================
    
    // Colors (RGB)
    // Character Color
    public static final Color EKKO_BLUE = CardHelper.getColor(50.0f, 225.0f, 225.0f).cpy();

    // Potion Colors in RGB
//    public static final Color PLACEHOLDER_POTION_LIQUID = CardHelper.getColor(209.0f, 53.0f, 18.0f); // Orange-ish Red
//    public static final Color PLACEHOLDER_POTION_HYBRID = CardHelper.getColor(255.0f, 230.0f, 230.0f); // Near White
//    public static final Color PLACEHOLDER_POTION_SPOTS = CardHelper.getColor(100.0f, 25.0f, 10.0f); // Super Dark Red/Brown

    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
  
    // Card backgrounds - The actual rectangular card.
    private static final String ATTACK_DEFAULT_GRAY = "ekkoTheBoyWhoShatteredTimeResources/images/512/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY = "ekkoTheBoyWhoShatteredTimeResources/images/512/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY = "ekkoTheBoyWhoShatteredTimeResources/images/512/bg_power_default_gray.png";
    
    private static final String ENERGY_ORB_DEFAULT_GRAY = "ekkoTheBoyWhoShatteredTimeResources/images/512/card_default_gray_orb.png";
    private static final String CARD_ENERGY_ORB = "ekkoTheBoyWhoShatteredTimeResources/images/512/card_small_orb.png";
    
    private static final String ATTACK_DEFAULT_GRAY_PORTRAIT = "ekkoTheBoyWhoShatteredTimeResources/images/1024/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY_PORTRAIT = "ekkoTheBoyWhoShatteredTimeResources/images/1024/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY_PORTRAIT = "ekkoTheBoyWhoShatteredTimeResources/images/1024/bg_power_default_gray.png";
    private static final String ENERGY_ORB_DEFAULT_GRAY_PORTRAIT = "ekkoTheBoyWhoShatteredTimeResources/images/1024/card_default_gray_orb.png";
    
    // Character assets
    private static final String THE_DEFAULT_BUTTON = "ekkoTheBoyWhoShatteredTimeResources/images/charSelect/DefaultCharacterButton.png";
    private static final String THE_DEFAULT_PORTRAIT = "ekkoTheBoyWhoShatteredTimeResources/images/charSelect/DefaultCharacterPortraitBG.png";
    public static final String THE_DEFAULT_SHOULDER_1 = "ekkoTheBoyWhoShatteredTimeResources/images/char/defaultCharacter/shoulder.png";
    public static final String THE_DEFAULT_SHOULDER_2 = "ekkoTheBoyWhoShatteredTimeResources/images/char/defaultCharacter/shoulder2.png";
    public static final String THE_DEFAULT_CORPSE = "ekkoTheBoyWhoShatteredTimeResources/images/char/defaultCharacter/corpse.png";
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "ekkoTheBoyWhoShatteredTimeResources/images/Badge.png";
    
    // Atlas and JSON files for the Animations
    public static final String THE_DEFAULT_SKELETON_ATLAS = "ekkoTheBoyWhoShatteredTimeResources/images/char/defaultCharacter/skeleton.atlas";
    public static final String THE_DEFAULT_SKELETON_JSON = "ekkoTheBoyWhoShatteredTimeResources/images/char/defaultCharacter/skeleton.json";
    
    // =============== MAKE IMAGE PATHS =================
    
    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }
    
    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/orbs/" + resourcePath;
    }
    
    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }
    
    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }
    
    // =============== /MAKE IMAGE PATHS/ =================
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================

    public EkkoMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
        
      /*
           (   ( /(  (     ( /( (            (  `   ( /( )\ )    )\ ))\ )
           )\  )\()) )\    )\()))\ )   (     )\))(  )\()|()/(   (()/(()/(
         (((_)((_)((((_)( ((_)\(()/(   )\   ((_)()\((_)\ /(_))   /(_))(_))
         )\___ _((_)\ _ )\ _((_)/(_))_((_)  (_()((_) ((_|_))_  _(_))(_))_
        ((/ __| || (_)_\(_) \| |/ __| __| |  \/  |/ _ \|   \  |_ _||   (_)
         | (__| __ |/ _ \ | .` | (_ | _|  | |\/| | (_) | |) |  | | | |) |
          \___|_||_/_/ \_\|_|\_|\___|___| |_|  |_|\___/|___/  |___||___(_)
      */
      
        setModID("ekkoTheBoyWhoShatteredTime");
        // cool
        // TODO: NOW READ THIS!!!!!!!!!!!!!!!:
        
        // 1. Go to your resources folder in the project panel, and refactor> rename theDefaultResources to
        // yourModIDResources.
        
        // 2. Click on the localization > eng folder and press ctrl+shift+r, then select "Directory" (rather than in Project)
        // replace all instances of theDefault with yourModID.
        // Because your mod ID isn't the default. Your cards (and everything else) should have Your mod id. Not mine.
        
        // 3. FINALLY and most importantly: Scroll up a bit. You may have noticed the image locations above don't use getModID()
        // Change their locations to reflect your actual ID rather than theDefault. They get loaded before getID is a thing.
        
        logger.info("Done subscribing");
        
        logger.info("Creating the color " + EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO.toString());
        
        BaseMod.addColor(EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO, EKKO_BLUE, EKKO_BLUE, EKKO_BLUE,
                EKKO_BLUE, EKKO_BLUE, EKKO_BLUE, EKKO_BLUE,
                ATTACK_DEFAULT_GRAY, SKILL_DEFAULT_GRAY, POWER_DEFAULT_GRAY, ENERGY_ORB_DEFAULT_GRAY,
                ATTACK_DEFAULT_GRAY_PORTRAIT, SKILL_DEFAULT_GRAY_PORTRAIT, POWER_DEFAULT_GRAY_PORTRAIT,
                ENERGY_ORB_DEFAULT_GRAY_PORTRAIT, CARD_ENERGY_ORB);
        
        logger.info("Done creating the color");
        
        
        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        theDefaultDefaultSettings.setProperty(ENABLE_PLACEHOLDER_SETTINGS, "FALSE"); // This is the default setting. It's actually set...
        try {
            SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            enablePlaceholder = config.getBool(ENABLE_PLACEHOLDER_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
        
    }
    
    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP
    
    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = EkkoMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = EkkoMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = EkkoMod.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO
    
    // ====== YOU CAN EDIT AGAIN ======
    
    
    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("========================= Initializing Default Mod. Hi. =========================");
        EkkoMod defaultmod = new EkkoMod();
        logger.info("========================= /Default Mod Initialized. Hello World./ =========================");
    }
    
    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================
    
    
    // =============== LOAD THE CHARACTER =================
    
    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME.toString());
        
        BaseMod.addCharacter(new EkkoTheBoyWhoShatteredTime("the Default", EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME),
                THE_DEFAULT_BUTTON, THE_DEFAULT_PORTRAIT, EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME);
        
        receiveEditPotions();
        logger.info("Added " + EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME.toString());
    }
    
    // =============== /LOAD THE CHARACTER/ =================
    
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        // Create the on/off button:
        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton("TO BE CHANGED",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                enablePlaceholder, // Boolean it uses
                settingsPanel, // The mod panel in which this button will be in
                (label) -> {}, // thing??????? idk
                (button) -> { // The actual button:
            
            enablePlaceholder = button.enabled; // The boolean true/false will be whether the button is enabled or not
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings);
                config.setBool(ENABLE_PLACEHOLDER_SETTINGS, enablePlaceholder);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        settingsPanel.addUIElement(enableNormalsButton); // Add the button to the settings panel. Button is a go.
        
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        
        // =============== EVENTS =================
        
        // This event will be exclusive to the City (act 2). If you want an event that's present at any
        // part of the game, simply don't include the dungeon ID
        // If you want to have a character-specific event, look at slimebound (CityRemoveEventPatch).
        // Essentially, you need to patch the game and say "if a player is not playing my character class, remove the event from the pool"
        //BaseMod.addEvent(IdentityCrisisEvent.ID, IdentityCrisisEvent.class, TheCity.ID);
        
        // =============== /EVENTS/ =================
        logger.info("Done loading badge Image and mod options");
    }
    
    // =============== / POST-INITIALIZE/ =================
    
    
    // ================ ADD POTIONS ===================
    
    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");
        
        // Class Specific Potion. If you want your potion to not be class-specific,
        // just remove the player class at the end (in this case the "TheDefaultEnum.THE_DEFAULT".
        // Remember, you can press ctrl+P inside parentheses like addPotions)
        BaseMod.addPotion(BottledSheen.class, Color.GOLD.cpy(), Color.CYAN.cpy(), null, BottledSheen.POTION_ID, EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME);
        BaseMod.addPotion(ResonatingPotion.class, Color.valueOf("0d429dff").cpy(), null, Color.CYAN.cpy(), ResonatingPotion.POTION_ID, EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME);
        BaseMod.addPotion(EquilibriumPotion.class, Color.DARK_GRAY.cpy(), Color.CHARTREUSE.cpy(), Color.CORAL.cpy(), EquilibriumPotion.POTION_ID, EkkoTheBoyWhoShatteredTime.Enums.EKKO_THE_BOY_WHO_SHATTERED_TIME);

        logger.info("Done editing potions");
    }
    
    // ================ /ADD POTIONS/ ===================
    
    
    // ================ ADD RELICS ===================
    
    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");
        
        // This adds a character specific relic. Only when you play with the mentioned color, will you get this relic.
        BaseMod.addRelicToCustomPool(new zDriveResonance(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new zDriveGreediness(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new TwistedClockwork(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new ChainArmlet(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new PairOfMasks(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new BronzeTube(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);
        BaseMod.addRelicToCustomPool(new ResonanceReceiver(), EkkoTheBoyWhoShatteredTime.Enums.COLOR_LIGHTNINGBLUE_EKKO);

        // This adds a relic to the Shared pool. Every character can find this relic.
        //BaseMod.addRelic(new PlaceholderRelic2(), RelicType.SHARED);
        
        // Mark relics as seen (the others are all starters so they're marked as seen in the character file
        //UnlockTracker.markRelicAsSeen(BottledPlaceholderRelic.ID);
        logger.info("Done adding relics!");
    }
    
    // ================ /ADD RELICS/ ===================


    // ================ ADD CARDS ===================
    
    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        //Ignore this
        pathCheck();
        // Add the Custom Dynamic Variables
        logger.info("Add variables");
        // Add the Custom Dynamic variables
        BaseMod.addDynamicVariable(new DefaultCustomVariable());
        BaseMod.addDynamicVariable(new DefaultSecondMagicNumber());
        
        logger.info("Adding cards");
        // Add the cards
        // Don't comment out/delete these cards (yet). You need 1 of each type and rarity (technically) for your game not to crash
        // when generating card rewards/shop screen items.

        BaseMod.addCard(new Strike());
        BaseMod.addCard(new Defend());
        BaseMod.addCard(new TimeMaster());
        BaseMod.addCard(new Powered());

        BaseMod.addCard(new PhaseDive());
        BaseMod.addCard(new HauntingGuise());
        BaseMod.addCard(new Knowledge());
        BaseMod.addCard(new WaitForKeyTiming());
        BaseMod.addCard(new NashorsTooth());
        BaseMod.addCard(new Unleashed());
        BaseMod.addCard(new AfterimageBait());
        BaseMod.addCard(new BansheesVeil());
        BaseMod.addCard(new LichBane());
        BaseMod.addCard(new Overflow());
        BaseMod.addCard(new WideSlice());
        BaseMod.addCard(new AfterimageDualStroke());
        BaseMod.addCard(new Dash());
        BaseMod.addCard(new DefensiveStance());
        BaseMod.addCard(new PlayTheBeat());
        BaseMod.addCard(new Leap());
        BaseMod.addCard(new RecallingPunch());
        BaseMod.addCard(new ComeAtMe());
        BaseMod.addCard(new DodgeTraining());
        BaseMod.addCard(new Playful());
        BaseMod.addCard(new URF());
        BaseMod.addCard(new AfterimageHit());
        BaseMod.addCard(new Timewinder());
        BaseMod.addCard(new Discharge());
        BaseMod.addCard(new HextechProtobelt());
        BaseMod.addCard(new NeedlesslyLargeRod());
        BaseMod.addCard(new AfterimageSetup());
        BaseMod.addCard(new ParallelConvergence());
        BaseMod.addCard(new Chronobreak());
        BaseMod.addCard(new ZhonyasHourglass());
        BaseMod.addCard(new TimeRecall());
        BaseMod.addCard(new Confident());
        BaseMod.addCard(new Greed());
        BaseMod.addCard(new NaggingBlow());
        BaseMod.addCard(new GuinsoosRageblade());
        BaseMod.addCard(new CatchUpTime());
        BaseMod.addCard(new AcademyAttraction());
        BaseMod.addCard(new SandstormSympathy());
        BaseMod.addCard(new ProjectPreference());
        BaseMod.addCard(new SktSynergy());
        BaseMod.addCard(new ItemsAfterimage());
        BaseMod.addCard(new Resocharge());
        //BaseMod.addCard(new PhasingArmor());
        BaseMod.addCard(new TrickOrTreatTrend());
        BaseMod.addCard(new Periocharge());
        BaseMod.addCard(new Disturblast());
        BaseMod.addCard(new Acceleration());
        BaseMod.addCard(new GrandeFinale());
        BaseMod.addCard(new MicCheck());
        BaseMod.addCard(new DelayedWork());
        BaseMod.addCard(new FeelIt());
        BaseMod.addCard(new AfterimageTease());
        BaseMod.addCard(new HailOfBlades());
        BaseMod.addCard(new PulsefirePooling());
        BaseMod.addCard(new Backtrack());
        BaseMod.addCard(new DuplicateTimelines());
        BaseMod.addCard(new KeepingMomentum());
        BaseMod.addCard(new CommittedStrike());
        BaseMod.addCard(new HauntedAfterimage());
        BaseMod.addCard(new SpHit());
        BaseMod.addCard(new VelocityConsuming());
        BaseMod.addCard(new Unchained());
        BaseMod.addCard(new IcebornGauntlet());
        BaseMod.addCard(new TrinityForce());
        BaseMod.addCard(new TimeIndustry());
        BaseMod.addCard(new QuickBreak());
        BaseMod.addCard(new KneeNSwordCombo());
        BaseMod.addCard(new Vibin());
        BaseMod.addCard(new LevelUp());
        BaseMod.addCard(new FluidEnergy());
        BaseMod.addCard(new OverloadingWeapon());
        BaseMod.addCard(new Process());
        BaseMod.addCard(new AfterimageTimeWrap());
        BaseMod.addCard(new LoadUp());
        BaseMod.addCard(new IceBreak());
        BaseMod.addCard(new SneakHit());
        BaseMod.addCard(new CalmingWaves());

        logger.info("Making sure the cards are unlocked.");
        // Unlock the cards
        // This is so that they are all "seen" in the library, for people who like to look at the card list
        // before playing your mod.
        UnlockTracker.unlockCard(TimeMaster.ID);

        
        logger.info("Done adding cards!");
    }
    
    // There are better ways to do this than listing every single individual card, but I do not want to complicate things
    // in a "tutorial" mod. This will do and it's completely ok to use. If you ever want to clean up and
    // shorten all the imports, go look take a look at other mods, such as Hubris.
    
    // ================ /ADD CARDS/ ===================

    @SpireEnum
    public static AbstractCard.CardTags RESONATE;
    public static AbstractCard.CardTags ITEM;

    
    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("You seeing this?");
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        
        // CardStrings
        BaseMod.loadCustomStringsFile(CardStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Card-Strings.json");
        
        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Power-Strings.json");
        
        // RelicStrings
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Relic-Strings.json");
        
        // Event Strings
        BaseMod.loadCustomStringsFile(EventStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Event-Strings.json");
        
        // PotionStrings
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Potion-Strings.json");
        
        // CharacterStrings
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Character-Strings.json");
        
        // OrbStrings
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Orb-Strings.json");
        
        logger.info("Done edittting strings");
    }
    
    // ================ /LOAD THE TEXT/ ===================

    public static ArrayList<TooltipInfo> resonanceTooltip; //define static list of tooltip info
    static { //static code block, is executed the first time the class is used
        resonanceTooltip = new ArrayList<TooltipInfo>(); //create new instance
        resonanceTooltip.add(new TooltipInfo("Resonance", "When at 3 stacks draw two cards, deal damage equal to your strength and gain 4 Block, can be activated only once per turn per enemy.")); //Add a tooltip to the list
    }
    public static ArrayList<TooltipInfo> itemTooltip; //define static list of tooltip info
    static { //static code block, is executed the first time the class is used
        itemTooltip = new ArrayList<TooltipInfo>(); //create new instance
        itemTooltip.add(new TooltipInfo("Items affected", "Banshee's veil NL Guinsoo's rageblade NL Haunting guise NL Hextech protobelt NL Iceborn gauntlet NL Lich bane NL Nashor's tooth NL Needlessly large rod NL Trinity force NL Zhonyas hourglass")); //Add a tooltip to the list
    }
    public static ArrayList<TooltipInfo> dupTooltip; //define static list of tooltip info
    static { //static code block, is executed the first time the class is used
        dupTooltip = new ArrayList<TooltipInfo>(); //create new instance
        dupTooltip.add(new TooltipInfo("Powers affected", "Common effects : NL Energized NL Draw more cards on next turn NL Next turn block NL Next turn lose strength NL Next turn lose dexterity NL Next turn gain strength NL Delayed resonance NL Delayed damage" +
                " NL NL Rare effects : NL Next turn death NL Gain an extra turn NL Phantasmal NL Nightmare NL Draw less cards on next turn")); //Add a tooltip to the list
    }
    
    // ================ LOAD THE KEYWORDS ===================
    
    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword
        
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/DefaultMod-Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);
        
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
            }
        }
    }
    
    // ================ /LOAD THE KEYWORDS/ ===================    
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}
