package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import model.Board;
import model.Const;
import model.Microcontroller;
import model.Section;
import utilities.SecureTokenizer;

public class BoardPersister{
	private Boolean recognisedPattern= false;
	
	public List<Board> parse(List<Path> fileNames) throws IOException{
		List<Board> parsedBoards= new ArrayList<>();
		
		for(Path file : fileNames){
			Board currentBoard=new Board();
			Reader reader= new FileReader(file.toFile());
			String line;
			BufferedReader br=new BufferedReader(reader);
			
			//HERE WE RETRIEVE BOARDS METADATA
			String boardName = Const.UNDEFINED_NAME,
					define,
					defineBoardNameToken,
					boardNameToken;
			while((line = br.readLine()) != null){
				if(line.trim().equals(Const.EMPTY)){
					continue;
				}
				StringTokenizer st= new StringTokenizer(line);
				
				define= SecureTokenizer.readToken(st);
				defineBoardNameToken= SecureTokenizer.readToken(st);
				SecureTokenizer.readToken(st, Const.BACKSLASH);
				boardNameToken= SecureTokenizer.readToken(st, Const.BACKSLASH);
				
				if(define.equals(Const.DEFINE) && defineBoardNameToken.equals(Const.BOARD_NAME_TOKEN)){
					boardName= boardNameToken.trim();
					//When we find the first occurrence, we stop.
					//Well,it should exist only 1 occurrence of BOARD_NAME for every board...
					break;
				}
			}
			currentBoard.nameProperty().set(boardName.trim());
			currentBoard.fileNameProperty().set(file.getFileName().toString());
			br.close();
			reader= new FileReader(file.toFile());
			br= new BufferedReader(reader);
			
			//NOW WE RETRIEVE PIN VALUES DATA
			String defineToken=Const.EMPTY, 
					pinNameToken=Const.EMPTY,
					pinValueToken=Const.EMPTY,
					pinComment=Const.EMPTY;

			while((line = br.readLine()) != null){
				if(line.trim().equals(Const.EMPTY)){
					continue;
				}
				StringTokenizer st= new StringTokenizer(line);
				
				//With this simple check, we avoid reading pins defined in #if...#endif blocks
				String initialToken= SecureTokenizer.readToken(st);
				if(initialToken.startsWith(Const.IF)){
					consumeIfBlock(br, line, currentBoard);
					recognisedPattern=true;
					continue;
				}else if(initialToken.startsWith(Const.MK4DUOBOARDS_SECTION_START)){
					String sectionName= initialToken.substring(Const.MK4DUOBOARDS_SECTION_START_NUM_OF_CHARS, initialToken.length());
					parseMK4DuoSection(sectionName, br, currentBoard);
					continue;
				}else if(initialToken.startsWith(Const.MK4DUOBOARDS_SECTION_END)){
					continue;
				}else if(initialToken.startsWith(Const.SINGLE_LINE_COMM_START)){
					consumeSingleLineComment(br, line, currentBoard);
					recognisedPattern=true;
					continue;
				}else if(initialToken.startsWith(Const.MUL_LINES_COMM_START)){
					consumeMultipleLineComment(br, line, currentBoard);
					recognisedPattern=true;
					continue;
				}else{
					defineToken= initialToken;
					pinNameToken= SecureTokenizer.readToken(st);
					pinValueToken= SecureTokenizer.readToken(st);
					pinComment= SecureTokenizer.readInlineCommentToken(st);
					
					if(defineToken.equals(Const.DEFINE)){
						recognisedPattern=true;
						String section= ConfigPersister.getSectionNameOf(pinNameToken);
						if(section != null){
							//When we find the first occurrence, we stop.
							//Well,it should exist only 1 occurrence for every pin...
							currentBoard.getPinByNameAndSection(pinNameToken, section).setValue(Integer.parseInt(pinValueToken));
							currentBoard.getPinByNameAndSection(pinNameToken, section).setComment(pinComment);
							
						}else if(pinNameToken.equals(Const.BOARD_NAME_TOKEN)==false && pinNameToken.equals(Const.KNOWN_BOARD_TOKEN)==false){
							//UnknownPin!!!
							currentBoard.setUnknownPins(currentBoard.getUnknownPins()+line.trim()+System.lineSeparator());
						}
					}
				}
			}
			
			if(this.recognisedPattern==true){
				parsedBoards.add(currentBoard);
			}
			recognisedPattern=false;
			br.close();
		}
		
		return parsedBoards;
	}
	
	private void parseMK4DuoSection(String sectionName, BufferedReader br, Board currentBoard) throws IOException {
		if(sectionName.equals(Const.MK4DUOBOARDS_BOARD_NAME_SECTION)){
			ignoreSection(br);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_CHIP_SECTION)){
			consumeChipSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_SERVOMOTORS_SECTION)){
			consumeServoMotorsSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_UNKNOWN_PINS_SECTION)){ 
			consumeUnknownPinsSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_IF_BLOCKS_SECTION)){
			consumeIfBlocksSection(br, currentBoard);
		}
		
	}

	private void consumeIfBlocksSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder sb= new StringBuilder();
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		currentBoard.setIfBlocks(sb.toString());
	}

	private void consumeUnknownPinsSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder sb= new StringBuilder();
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		currentBoard.setUnknownPins(sb.toString());
		
	}

	private void consumeServoMotorsSection(BufferedReader br, Board currentBoard) throws IOException {
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			StringTokenizer st= new StringTokenizer(line);
			String defineToken= SecureTokenizer.readToken(st);
			String pinNameToken= SecureTokenizer.readToken(st);
			String pinValueToken= SecureTokenizer.readToken(st);
			String pinComment= SecureTokenizer.readInlineCommentToken(st);
			
			if(defineToken.equals(Const.DEFINE)){
				recognisedPattern=true;
				String section= ConfigPersister.getSectionNameOf(pinNameToken);
				if(section != null){
					//When we find the first occurrence, we stop.
					//Well,it should exist only 1 occurrence for every pin...
					currentBoard.getPinByNameAndSection(pinNameToken, section).setValue(Integer.parseInt(pinValueToken));
					currentBoard.getPinByNameAndSection(pinNameToken, section).setComment(pinComment);
					
				}
			}
		}
		
	}
	
	private void consumeChipSection(BufferedReader br, Board currentBoard) throws IOException {
		String firstLine;
		while((firstLine=br.readLine().trim())!=null){
			if(firstLine.isEmpty()==false){
				break;
			}
		}
		for(String chipName: ConfigPersister.getChipNames()){
			Microcontroller chip= ConfigPersister.getChip(chipName);
			if(chip.getCheckCode().startsWith(firstLine)){
				currentBoard.setMicrocontroller(chip);
			}
		}
		
		while((firstLine=br.readLine())!=null && firstLine.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			
		}
	}

	private void ignoreSection(BufferedReader br) throws IOException {
		String line;
		while((line= br.readLine())!=null){
			if(line.startsWith(Const.MK4DUOBOARDS_SECTION_END)){
				return;
			}
		}
		
	}


	private void consumeIfBlock(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		Integer numOfIf=1, numOfEndif=0;
		String line;
		StringBuilder sb= new StringBuilder(lineAlreadyRead);
		sb.append(System.lineSeparator());
		while((line=br.readLine())!=null){
			StringTokenizer st= new StringTokenizer(line);
			String token;
			token= SecureTokenizer.readToken(st);
			
			if(token.startsWith(Const.IF)){
				numOfIf++;
			}else if(token.equals(Const.ENDIF)){
				numOfEndif++;
			}
			sb.append(line);
			sb.append(System.lineSeparator());
			if(numOfIf==numOfEndif){
				sb.append(System.lineSeparator());
				board.setIfBlocks(board.getIfBlocks()+sb.toString());
				return;
			}
		}
		
	}
	
	private void consumeSingleLineComment(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		StringBuilder sb= new StringBuilder(lineAlreadyRead.trim());
		sb.append(System.lineSeparator());
		board.setIfBlocks(board.getIfBlocks()+sb.toString());
		return;		
	}
	
	private void consumeMultipleLineComment(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		String line;
		StringBuilder sb= new StringBuilder(lineAlreadyRead);
		if(board.getDescription() != null || board.getDescription().equals(Const.EMPTY)){
			sb.append(System.lineSeparator());
		}
		while((line=br.readLine())!=null){
			sb.append(line);
			sb.append(System.lineSeparator());
			if(line.endsWith(Const.MUL_LINES_COMM_END)){
				if(lineAlreadyRead.startsWith(Const.DESCRIPTION_START)){
					board.setDescription(board.getDescription()+sb.toString());
				}else{
					sb.append(System.lineSeparator());
					board.setIfBlocks(board.getIfBlocks()+sb.toString());
				}
				return;
			}
		}
		
	}
	

	public void export(List<Board> data, Path rootDir) throws IOException {
		if(Files.exists(rootDir)==false || Files.isDirectory(rootDir)==false){
			throw new IOException();
		}
		for(Board board: data){
			Path currentFile= rootDir.resolve(board.getFileName());
			Writer w= new FileWriter(currentFile.toFile());
			BufferedWriter fw=new BufferedWriter(w);
			
			writeDescription(board, fw);
			fw.newLine();
			writeMicrocontrollerCheckCode(board, fw);
			fw.newLine();
			writeKnownBoardToken(fw);
			fw.newLine();
			writeBoardNameCode(board, fw);
			fw.newLine();
			writeEverySectionExceptForServomotors(board, fw);
			writeServomotors(board,fw);
			fw.newLine();
			writeUnknownPins(board, fw);
			fw.newLine();
			writeIfBlocks(board, fw);
			
			fw.flush();
			fw.close();
		}
		
	}

	private void writeDescription(Board board, BufferedWriter fw) throws IOException {
		if(board.getDescription().trim().isEmpty()==false){
			fw.write(board.getDescription().trim());
			fw.newLine();
		}
	}
	
	private void writeKnownBoardToken(BufferedWriter fw) throws IOException  {
		fw.write(Const.DEFINE+Const.SPACE+Const.KNOWN_BOARD_TOKEN+Const.SPACE+"1");
		fw.newLine();
	}
	
	private void writeMicrocontrollerCheckCode(Board board, BufferedWriter fw) throws IOException  {
		if(board.getMicrocontroller().getCheckCode().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_CHIP_SECTION);
			fw.newLine();
			fw.write(board.getMicrocontroller().getCheckCode().trim());
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.newLine();
		}
	}
	
	private void writeBoardNameCode(Board board, BufferedWriter fw) throws IOException  {
		fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_BOARD_NAME_SECTION);
		fw.newLine();
		fw.write(Const.IFNDEF+Const.SPACE+Const.BOARD_NAME_TOKEN);
		fw.newLine();
		fw.write(Const.TAB+Const.DEFINE+Const.SPACE+Const.BOARD_NAME_TOKEN+Const.SPACE+Const.BACKSLASH+board.getName()+Const.BACKSLASH);
		fw.newLine();
		fw.write(Const.ENDIF);
		fw.newLine();
		fw.write(Const.MK4DUOBOARDS_SECTION_END);
		fw.newLine();
	}
	
	private void writeEverySectionExceptForServomotors(Board board, BufferedWriter fw) throws IOException  {
		fw.newLine();
		for(Section section: ConfigPersister.getKnownPins()){
			if(section.getName().startsWith(Const.SERVOS_SECTION_START)==false){
				fw.write(Const.MK4DUOBOARDS_SECTION_START+ section.getName());
				fw.newLine();
				for(String pinName: section.getPins()){
					fw.write(Const.DEFINE+Const.SPACE+pinName+Const.SPACE+board.getPinByNameAndSection(pinName, section.getName()).getValue());
					fw.newLine();
				}
				fw.newLine();
			}
		}
	}
	
	private void writeServomotors(Board board, BufferedWriter fw) throws IOException  {
		fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_SERVOMOTORS_SECTION);
		fw.newLine();
		Integer indexOfServo= 0;
		for(Section section: ConfigPersister.getKnownPins()){
			if(section.getName().startsWith(Const.SERVOS_SECTION_START)){
				for(String pinName: section.getPins()){
					for(int i=0; i< indexOfServo; ++i){
						fw.write("\t");
					}
					fw.write(Const.IF_NUM_SERVOS_GREATER_THAN + Const.SPACE+ indexOfServo.toString());
					fw.newLine();
					for(int i=0; i< indexOfServo+1; ++i){
						fw.write("\t");
					}
					fw.write(Const.DEFINE+Const.SPACE+pinName+Const.SPACE+board.getPinByNameAndSection(pinName, section.getName()).getValue());
					fw.newLine();
					indexOfServo++;
				}
			}
		}
		indexOfServo--;
		for(int i=indexOfServo; i>= 0; --i){
			for(int j=0; j< i; ++j){
				fw.write("\t");
			}
			fw.write(Const.ENDIF);
			fw.newLine();
		}
		fw.write(Const.MK4DUOBOARDS_SECTION_END);
		fw.newLine();
	}
	
	private void writeUnknownPins(Board board, BufferedWriter fw) throws IOException  {
		if(board.getUnknownPins().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_UNKNOWN_PINS_SECTION);
			fw.newLine();
			fw.write(board.getUnknownPins().trim());
			fw.newLine();
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.newLine();
		}
	}
	
	private void writeIfBlocks(Board board, BufferedWriter fw) throws IOException  {
		if(board.getIfBlocks().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_IF_BLOCKS_SECTION);
			fw.newLine();
			fw.write(board.getIfBlocks().trim());
			fw.newLine();
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.newLine();
		}
	}
}
