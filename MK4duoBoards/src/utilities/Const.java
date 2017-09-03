package utilities;

public class Const {
	public static final String //MODEL
							    UNDEFINED_NAME= "_undefined_",
							    UNDEFINED_FILENAME= "-1.h",
							    UNDEFINED_MICROCONTROLLER= "NO_CHIP_SELECTED", 
	
							    //BOARDPERSISTER
								EXTRUDERS_SECTION_START= "EXTRUDER_",
							    SERVOS_SECTION_START= "SERVO_",
								BOARD_NAME_TOKEN= "BOARD_NAME",
								KNOWN_BOARD_TOKEN= "KNOWN_BOARD",
								DESCRIPTION_START= "/***",
								MK4DUOBOARDS_SECTION_START= "//###",
								MK4DUOBOARDS_SECTION_END= "//@@@",
								MK4DUOBOARDS_BOARD_NAME_SECTION= "BOARD_NAME",
								MK4DUOBOARDS_CHIP_SECTION= "CHIP",
								MK4DUOBOARDS_SERVOMOTORS_SECTION= "SERVOS",
								MK4DUOBOARDS_UNKNOWN_PINS_SECTION= "UNKNOWN_PINS",
								MK4DUOBOARDS_IF_BLOCKS_SECTION= "IF_BLOCKS",
								IF_NUM_SERVOS_GREATER_THAN= "#if NUM_SERVOS >",
								
							    //CONFIGPERSISTER
							    CONFIG_DIR= ".MK4duoBoards",
							    CONFIG_FILE= "config.txt",
							    KNOWN_PINS_FILE= "knownPins.txt",
							    MICROCONTROLLERS_FILE= "microcontrollers.txt",
							    LOCALE_TOKEN= "Locale:",
							    LASTDIR_FILES_TOKEN= "LastDirFiles:",
							    LASTDIR_DB_TOKEN= "LastDirDB:",	
							    START_TOKEN_PREFIX = "###",
					    		IS_ANALOG_TOKEN= "[analog]",
								ENUM_TOKEN_START= "@@@",
								ENUM_TO_REPLACE_TOKEN= "@",
							   
							    //GENERAL PURPOSE
								EOL="\n",
							    EMPTY="",
							    SPACE=" ",
							    DOUBLE_SPACE= "  ",
							    TAB="\t", //Actually not used!!! It was replaced by DOUBLE_SPACE... (See BoardPersister::export)
							    QUOTE= "\"",
							    
							    //C++ HEADER FILE TOKENS
					    		DEFINE= "#define",
								IF= "#if",
								IFNDEF= "#ifndef",
								ENDIF= "#endif",
								DISABLED= "DISABLED",
								SINGLE_LINE_COMM_START="//",
								MUL_LINES_COMM_START= "/*",
								MUL_LINES_COMM_END= "*/";
	
	public static final Integer //MODEL
								UNDEFINED_PIN= -1,
								
								//CONFIGPERSISTER
								NUM_OF_LOCALE_TOKENS= 3,
								NUM_OF_LASTDIR_FILES_MINIMUM_TOKENS= 2,
								NUM_OF_LASTDIR_DB_MINIMUM_TOKENS= 2,
								NUM_OF_CHIP_START_TOKENS= 1,
								START_TOKEN_PREFIX_NUM_OF_CHARS = 3,
								
								//BOARDPERSISTER
								MK4DUOBOARDS_SECTION_START_NUM_OF_CHARS= 5;

	public static final String APP_NAME = "MK4duoBoards";

	public static final String GITHUB_URL = "https://github.com/iosonopersia/MK4duoBoards";

}
