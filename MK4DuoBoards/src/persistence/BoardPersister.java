package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import model.Board;
import model.Const;
import model.Section;
import utilities.SectionParser;
import utilities.SecureTokenizer;

public class BoardPersister{
	
/*****************************************************************************************************************************
 * 													--INPUT--
 ****************************************************************************************************************************/	
	private Boolean recognisedPattern= false;
	
	public List<Board> parse(List<File> files) {
		List<Board> parsedBoards= new ArrayList<>();
		
		for(File file : files){
			Board currentBoard=new Board();
			try (BufferedReader br= Files.newBufferedReader(file.toPath())) {
				currentBoard.setName(Const.UNDEFINED_NAME);
				currentBoard.setFileName(file.toPath().getFileName().toString());
				
				String defineToken=Const.EMPTY, 
						pinNameToken=Const.EMPTY,
						pinValueToken=Const.EMPTY,
						pinComment=Const.EMPTY;
				
				String line;
				while((line = br.readLine()) != null){
					if(line.trim().equals(Const.EMPTY)){
						continue;
					}
					StringTokenizer st= new StringTokenizer(line);
					
					//This detects both "#if DISABLED(BOARD_NAME)" and "#ifndef BOARD_NAME"
					if(line.contains(Const.IF) && line.contains(Const.BOARD_NAME_TOKEN)){
						consumeIfNDefBoardName(br, currentBoard);
						continue;
					}
					
					String initialToken= SecureTokenizer.readToken(st);
					
					if(initialToken.startsWith(Const.IF)){
						consumeIfBlock(br, line, currentBoard);
						recognisedPattern=true;
						continue;
					}else if(initialToken.startsWith(Const.MK4DUOBOARDS_SECTION_START)){
						String sectionName= initialToken.substring(Const.MK4DUOBOARDS_SECTION_START_NUM_OF_CHARS, initialToken.length());
						SectionParser.parseMK4DuoSection(sectionName, br, currentBoard);
						recognisedPattern= true;
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
								currentBoard.setUnknownPins(currentBoard.getUnknownPins()+line.trim()+Const.EOL);
							}
						}
					}
				}
				
				if(this.recognisedPattern==true){
					parsedBoards.add(currentBoard);
					recognisedPattern=false;
				}
				
			}catch (IOException e){
				//Something went wrong with this file...
				//The best thing to do is continue with the remaining files until we have finished them.
				continue;
			}
		}
		
		return parsedBoards;
	}
	
	private void consumeIfNDefBoardName(BufferedReader br, Board board) throws IOException{
		String line,
			   defineToken= Const.EMPTY,
			   boardNameToken= Const.EMPTY,
			   boardNameString= Const.EMPTY;
		while((line= br.readLine())!=null){
			StringTokenizer st= new StringTokenizer(line);
			defineToken= SecureTokenizer.readToken(st);
			boardNameToken=SecureTokenizer.readToken(st);
			
			if(defineToken.equals(Const.DEFINE) && boardNameToken.equals(Const.BOARD_NAME_TOKEN)){
				//We've found the BOARD_NAME token!
				SecureTokenizer.readToken(st, Const.QUOTE);
				boardNameString= SecureTokenizer.readToken(st, Const.QUOTE);
				board.setName(boardNameString.trim());
			}else if(defineToken.equals(Const.ENDIF)){
				return;
			}
		}
		
	}

	private void consumeIfBlock(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		Integer numOfIf=1, numOfEndif=0;
		String line;
		StringBuilder sb= new StringBuilder(lineAlreadyRead);
		sb.append(Const.EOL);
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
			sb.append(Const.EOL);
			if(numOfIf==numOfEndif){
				sb.append(Const.EOL);
				board.setIfBlocks(board.getIfBlocks()+sb.toString());
				return;
			}
		}
	}
	
	private void consumeSingleLineComment(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		StringBuilder sb= new StringBuilder(lineAlreadyRead.trim());
		sb.append(Const.EOL);
		board.setIfBlocks(board.getIfBlocks()+sb.toString());
		return;		
	}
	
	private void consumeMultipleLineComment(BufferedReader br, String lineAlreadyRead, Board board) throws IOException {
		String line;
		StringBuilder sb= new StringBuilder(lineAlreadyRead);
		if(board.getDescription() != null || board.getDescription().equals(Const.EMPTY)){
			sb.append(Const.EOL);
		}
		while((line=br.readLine())!=null){
			sb.append(line);
			sb.append(Const.EOL);
			if(line.endsWith(Const.MUL_LINES_COMM_END)){
				if(lineAlreadyRead.startsWith(Const.DESCRIPTION_START)){
					board.setDescription(board.getDescription()+sb.toString());
				}else{
					sb.append(Const.EOL);
					board.setIfBlocks(board.getIfBlocks()+sb.toString());
				}
				return;
			}
		}
		
	}
	
/*****************************************************************************************************************************
 * 														--OUTPUT--
 ****************************************************************************************************************************/
	
	public void export(List<Board> data, Path rootDir) throws IOException {
		if(Files.exists(rootDir)==false || Files.isDirectory(rootDir)==false){
			throw new IOException();
		}
		for(Board board: data){
			Path currentFile= rootDir.resolve(board.getFileName());
			Writer w= new FileWriter(currentFile.toFile());
			BufferedWriter fw=new BufferedWriter(w);
			
			writeDescription(board, fw);
			fw.write(Const.EOL);
			writeMicrocontrollerCheckCode(board, fw);
			fw.write(Const.EOL);
			writeKnownBoardToken(fw);
			fw.write(Const.EOL);
			//INFO: initially the indentation format was "\t"
			//This was changed to a double space to accomodate
			//the request of MagoKimbra, in the attempt to
			//adopt the same format of the other MK4duo files.
			writeBoardNameCode(board, fw, Const.DOUBLE_SPACE);
			fw.write(Const.EOL);
			writeEverySectionExceptForServomotors(board, fw);
			writeServomotors(board, fw, Const.DOUBLE_SPACE);
			fw.write(Const.EOL);
			writeUnknownPins(board, fw);
			fw.write(Const.EOL);
			writeIfBlocks(board, fw);
			
			fw.flush();
			fw.close();
		}
		
	}

	private void writeDescription(Board board, BufferedWriter fw) throws IOException {
		if(board.getDescription().trim().isEmpty()==false){
			fw.write(board.getDescription().trim());
			if(board.getDescription().trim().endsWith(Const.EOL)==false){
				fw.write(Const.EOL);
			}
		}
	}
	
	private void writeKnownBoardToken(BufferedWriter fw) throws IOException  {
		fw.write(Const.DEFINE+Const.SPACE+Const.KNOWN_BOARD_TOKEN+Const.SPACE+"1");
		fw.write(Const.EOL);
	}
	
	private void writeMicrocontrollerCheckCode(Board board, BufferedWriter fw) throws IOException  {
		if(board.getMicrocontroller().getCheckCode().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_CHIP_SECTION);
			fw.write(Const.EOL);
			fw.write(board.getMicrocontroller().getCheckCode().trim());
			if(board.getMicrocontroller().getCheckCode().trim().endsWith(Const.EOL)==false){
				fw.write(Const.EOL);
			}
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.write(Const.EOL);
		}
	}
	
	private void writeBoardNameCode(Board board, BufferedWriter fw, String indentation) throws IOException  {
		fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_BOARD_NAME_SECTION);
		fw.write(Const.EOL);
		fw.write(Const.IF+Const.SPACE+Const.DISABLED+"("+Const.BOARD_NAME_TOKEN+")");
		fw.write(Const.EOL);
		fw.write(indentation+Const.DEFINE+Const.SPACE+Const.BOARD_NAME_TOKEN+Const.SPACE+Const.QUOTE+board.getName()+Const.QUOTE);
		fw.write(Const.EOL);
		fw.write(Const.ENDIF);
		fw.write(Const.EOL);
		fw.write(Const.MK4DUOBOARDS_SECTION_END);
		fw.write(Const.EOL);
	}
	
	private void writeEverySectionExceptForServomotors(Board board, BufferedWriter fw) throws IOException  {
		fw.write(Const.EOL);
		for(Section section: ConfigPersister.getKnownPins()){
			if(section.getName().startsWith(Const.SERVOS_SECTION_START)==false){
				fw.write(Const.MK4DUOBOARDS_SECTION_START+ section.getName());
				fw.write(Const.EOL);
				for(String pinName: section.getPins()){
					fw.write(Const.DEFINE+Const.SPACE+pinName+Const.SPACE+board.getPinByNameAndSection(pinName, section.getName()).getValue().toString());
					fw.write(Const.EOL);
				}
				fw.write(Const.EOL);
			}
		}
	}
	
	private void writeServomotors(Board board, BufferedWriter fw, String indentation) throws IOException  {
		fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_SERVOMOTORS_SECTION);
		fw.write(Const.EOL);
		Integer indexOfServo= 0;
		for(Section section: ConfigPersister.getKnownPins()){
			if(section.getName().startsWith(Const.SERVOS_SECTION_START)){
				for(String pinName: section.getPins()){
					for(int i=0; i< indexOfServo; ++i){
						fw.write(indentation);
					}
					fw.write(Const.IF_NUM_SERVOS_GREATER_THAN + Const.SPACE+ indexOfServo.toString());
					fw.write(Const.EOL);
					for(int i=0; i< indexOfServo+1; ++i){
						fw.write(indentation);
					}
					fw.write(Const.DEFINE+Const.SPACE+pinName+Const.SPACE+board.getPinByNameAndSection(pinName, section.getName()).getValue());
					fw.write(Const.EOL);
					indexOfServo++;
				}
			}
		}
		indexOfServo--;
		for(int i=indexOfServo; i>= 0; --i){
			for(int j=0; j< i; ++j){
				fw.write(indentation);
			}
			fw.write(Const.ENDIF);
			fw.write(Const.EOL);
		}
		fw.write(Const.MK4DUOBOARDS_SECTION_END);
		fw.write(Const.EOL);
	}
	
	private void writeUnknownPins(Board board, BufferedWriter fw) throws IOException  {
		if(board.getUnknownPins().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_UNKNOWN_PINS_SECTION);
			fw.write(Const.EOL);
			fw.write(board.getUnknownPins().trim());
			if(board.getUnknownPins().trim().endsWith(Const.EOL)==false){
				fw.write(Const.EOL);
			}
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.write(Const.EOL);
		}
	}
	
	private void writeIfBlocks(Board board, BufferedWriter fw) throws IOException  {
		if(board.getIfBlocks().trim().isEmpty()==false){
			fw.write(Const.MK4DUOBOARDS_SECTION_START+Const.MK4DUOBOARDS_IF_BLOCKS_SECTION);
			fw.write(Const.EOL);
			fw.write(board.getIfBlocks().trim());
			if(board.getIfBlocks().trim().endsWith(Const.EOL)==false){
				fw.write(Const.EOL);
			}
			fw.write(Const.MK4DUOBOARDS_SECTION_END);
			fw.write(Const.EOL);
		}
	}
}
