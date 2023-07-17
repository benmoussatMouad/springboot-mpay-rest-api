package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.FieldCheckDto;
import com.springboot.mpaybackend.payload.TmFileCheckDto;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.service.TmService;
import com.springboot.mpaybackend.utils.StringProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TmServiceImpl implements TmService {


    BankRepository bankRepository;
    AgencyRepository agencyRepository;
    String[] lines;

    public TmServiceImpl(BankRepository bankRepository, AgencyRepository agencyRepository) {
        this.bankRepository = bankRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    public TmFileCheckDto verifyFileContent(String content) {
        this.lines = content.split( System.lineSeparator() );
        if( lines.length < 3 ) {
            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "The file should have 3 lines or more" );
        }
        TmFileCheckDto dto = new TmFileCheckDto();
        dto.setAllCorrect( true );

        try {
            //Check header record
            dto = checkHeaderRecord( lines[0], dto );

            // Check second record
            dto = checkSecondRecord( lines[1], dto );

            // Check last record
            dto = checkLastRecord( lines[2], dto );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    private TmFileCheckDto checkLastRecord(String line, TmFileCheckDto dto) {

        if( checkIsNumeric( line, dto.getRecordType3(), 0, 3, "Record type must be of value 999" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getRecordsNumber(), 3, 10, "Number of records must be a numerical value in 7 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 10 ) != 'X' ) {
            dto.getLastRecordEndCharacter().setCorrect( false );
            dto.getLastRecordEndCharacter().setLine( 2 );
            dto.getLastRecordEndCharacter().setPositionStart( 10 );
            dto.getLastRecordEndCharacter().setPositionEnd( 10 );
            dto.getLastRecordEndCharacter().setFeedback( "End character must be X" );
            dto.getLastRecordEndCharacter().setValue( String.valueOf( line.charAt( 10 ) ) );
            dto.setAllCorrect( false );
        }

        return dto;
    }

    private TmFileCheckDto checkSecondRecord(String line, TmFileCheckDto dto) {

        if( checkIsNumeric( line, dto.getRecordType2(), 0, 3, "Record type should be a numeric value in 3 positions" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumeric( line, dto.getTerminalSequentialNumber(), 3, 10, "Terminal sequential number must be a numeric value in 7 positions" ) ) {
            dto.setAllCorrect( false );
        }
        if( line.charAt( 10 ) != 'A' && line.charAt( 10 ) != 'C' && line.charAt( 10 ) != 'D' ) {
            dto.getTerminalUpdateCode().setCorrect( false );
            dto.getTerminalUpdateCode().setPositionStart( 10 );
            dto.getTerminalUpdateCode().setPositionEnd( 10 );
            dto.getTerminalUpdateCode().setLine( 2 );
            dto.getTerminalUpdateCode().addFeedback( "Terminal update code must be A or C or D" );
            dto.getTerminalUpdateCode().setValue( String.valueOf( line.charAt( 10 ) ) );
        }

        if(checkIsNumeric( line, dto.getMerchantContractNumber(), 11, 26, "Merchant contract number must be an numeric value with 15 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericOrBlank( line, dto.getTerminalId(), 26, 41, "Terminal Id must a alphanumeric value in 15 positions ot blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getTerminalLabel(), 41, 71, "Terminal Label must be a alphanumeric value in 30 poisitions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine1(), 71, 111, "Address line 1 should be an alphanumeric value in 40 position" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getAddressLine3(), 111, 113, "Address line 3 should be a numeric value in 2 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine4(), 113, 133, "Address line 4 (Nom commune) should be a numeric value in 3 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine5(), 133, 153, "Address line 5 (Nom de la Daira) should be a numeric value in 20 poisitons" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getAddressLine6(), 153, 156, "Adress line 6 (Code wilaya) must be a numeric value in 3 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine7(), 156, 171, "Address line 7 (Libellé wilaya) should be an alphanumeric value in 15 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getAddressLine8(), 171, 176, "Address line 8 (Code postale) must be a numeric value in 5 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getTerminalPhoneNumber(), 176, 191, "Terminal phone number must be a numeric value in 15 poistions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericOrBlank( line, dto.getTerminalFaxNumber(), 191, 206, "Terminal fax number must be a numeric value in 15 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if(checkIsNumericOrBlank( line, dto.getTerminalMobileNumber(), 206, 221, "Terminal mobile phone number must be a numeric value in 15 positions or blank " ) ) {
        }

        if( checkAlphanumericAndSpecialCharacters( line, dto.getTerminalEmailAddress(), 221, 271, "Email address must an alphanumeric value with special characters in a maximum of 50 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkTrxCharacter( line, 271, dto.getTrxDebit(), "Trx Debit must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 272, dto.getTrxRemb(), "Trx Remb must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 273, dto.getTrxAnnul(), "Trx Annul must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 274, dto.getTrxSolde(), "Trx Solde must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 275, dto.getTrxPAutor(), "Trx P Autor must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 276, dto.getTrxTel(), "Trx Tel must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 277, dto.getTrxRetrait(), "Trx Retrait must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 278, dto.getTrxCashAdvance(), "Trx Cash Advance must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkTrxCharacter( line, 279, dto.getTrxPaiementFacture(), "Trx Paiement facture must be a 0 or 1" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumeric( line, dto.getHourStart(), 280, 284, "Hour Start must be a numeric value in 4 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getHourEnd(), 284, 288, "Hour End must be a numeric value in 4 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 288 ) != 'A' && line.charAt( 288 ) != 'D' ) {
            dto.getTerminalCardTypeUpdate().setCorrect(false);
            dto.getTerminalCardTypeUpdate().setValue(String.valueOf(line.charAt( 288 )));
            dto.getTerminalCardTypeUpdate().setLine(2);
            dto.getTerminalCardTypeUpdate().setPositionStart(288);
            dto.getTerminalCardTypeUpdate().setPositionEnd(288);
            dto.getTerminalCardTypeUpdate().setFeedback("Terminal card type should be A or D");
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType0(), 289, 291, 0, "Card type 0 must be of value 'C0'", false ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumeric( line, dto.getLimit0(), 291, 300, "Limit 0 must a numerical value in 9 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType1(), 300, 302, 1, "Card type 1 must be of value 'C1' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit1(), 302, 311, "Limit 1 must be a numerical value in 9 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType2(), 311, 313, 2, "Card type 2 must be of value 'C2' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit2(), 313, 322, "limit 2 must be a numerical value or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType3(), 322, 324, 3, "Card type 3 must be of value 'C3' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit3(), 324, 333, "Limit 3 must be a numerical value in 9 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType4(), 333, 335, 4, "Card type 4 must be of value 'C4' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit4(), 335, 344, "Limit 4 must be a numerical value in 9 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType5(), 344, 346, 4, "Card type 5 must be of value 'C5' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit5(), 346, 355, "Limit 5 must be a numerical value in 9 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkCardType( line, dto.getCardType6(), 355, 357, 4, "Card type 6 must be of value 'C6' or blank", true ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumericOrBlank( line, dto.getLimit6(), 357, 366, "Limit 6 must be a numerical value in 9 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getTerminalType(), 366, 368, "Terminal type must be a numerical value in two positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 368 ) != 'X' ) {
            dto.getSecondRecordEndCharacter().setCorrect( false );
            dto.getSecondRecordEndCharacter().setLine( 2 );
            dto.getSecondRecordEndCharacter().setPositionStart( 368 );
            dto.getSecondRecordEndCharacter().setPositionEnd( 368 );
            dto.getSecondRecordEndCharacter().setFeedback( "End character must be X" );
            dto.getSecondRecordEndCharacter().setValue( String.valueOf( line.charAt( 368 ) ) );
            dto.setAllCorrect( false );
        }

        return dto;
    }

    private boolean checkCardType(String line, FieldCheckDto cardTypeField, int positionStart, int positionEnd, int cardType, String feedback, boolean isOptional) {

        if( !isOptional ) {
            if( !line.substring( positionStart, positionEnd ).equals( "C" + cardType ) ) {
                cardTypeField.setCorrect( false );
                cardTypeField.setLine( Arrays.asList( this.lines ).indexOf( line ) + 1 );
                cardTypeField.setPositionStart( positionStart );
                cardTypeField.setPositionEnd( positionEnd -1 );
                cardTypeField.setValue( line.substring( positionStart, positionEnd ) );
                cardTypeField.addFeedback( feedback );
                return true;
            }
        } else {
            if( !line.substring( positionStart, positionEnd ).equals( "C" + cardType ) &&  line.substring( positionStart, positionEnd ).isBlank()) {
                cardTypeField.setCorrect( false );
                cardTypeField.setLine( Arrays.asList( this.lines ).indexOf( line ) + 1 );
                cardTypeField.setPositionStart( positionStart );
                cardTypeField.setPositionEnd( positionEnd -1 );
                cardTypeField.setValue( line.substring( positionStart, positionEnd ) );
                cardTypeField.addFeedback( feedback );
                return true;
            }
        }
        return false;
    }

    private boolean checkTrxCharacter(String line, int position, FieldCheckDto trxField, String feedback) {
        if( line.charAt( position ) != '0' && line.charAt( position ) != '1' ) {
            trxField.setCorrect( false );
            trxField.setLine( Arrays.asList( this.lines ).indexOf( line ) + 1 );
            trxField.setPositionStart( position );
            trxField.setPositionEnd( position );
            trxField.setValue( String.valueOf( line.charAt( position ) ) );
            trxField.addFeedback( feedback );
            return true;
        }
        return false;
    }

    private TmFileCheckDto checkHeaderRecord(String line, TmFileCheckDto dto) {
        if( checkIsNumeric( line, dto.getRecordType(), 0, 3, "Record type should 000" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkAlphanumeric( line, dto.getFileName(), 3, 21, "File name should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }
        if( checkIsNumeric( line, dto.getBankBinIso(), 21, 27, "BIN ISO of bank sould be numeric value in 8 positions" ) ) {
            dto.setAllCorrect( false );
        }
        if( line.charAt( 27 ) != 'X' ) {
            dto.getHeaderRecordEndCharacter().setCorrect( false );
            dto.getHeaderRecordEndCharacter().setPositionStart( 27 );
            dto.getHeaderRecordEndCharacter().setPositionEnd( 27 );
            dto.getHeaderRecordEndCharacter().setLine( 1 );
            dto.getHeaderRecordEndCharacter().addFeedback( "Last character should be X" );
        }
        return dto;
    }


    private boolean checkAlphanumericAndSpecialCharacters(String line, FieldCheckDto field, int positionStart, int positionEnd, String feedback) {

        boolean returnValue = false;

        if( !StringProcessor.isAlphaNumericWithSpecialChars( line.substring( positionStart, positionEnd ) ) ) {
            field.setCorrect( false );
            field.setLine( Arrays.asList( this.lines ).indexOf( line ) +1 );
            field.setPositionStart( positionStart );
            field.setPositionEnd( positionEnd -1 );
            field.addFeedback( feedback );
            returnValue = true;
        }
        field.setValue( line.substring( positionStart, positionEnd ) );
        return returnValue;
    }

    private boolean checkIsNumeric(String line, FieldCheckDto field, int positionStart, int positionEnd, String feedback) {

        boolean returnValue = false;

        if( !StringProcessor.isNumeric( line.substring( positionStart, positionEnd ) ) ) {
            field.setCorrect( false );
            field.setLine( Arrays.asList( this.lines ).indexOf( line ) +1 );
            field.setPositionStart( positionStart );
            field.setPositionEnd( positionEnd -1 );
            field.addFeedback( feedback );
            returnValue = true;
        }
        field.setValue( line.substring( positionStart, positionEnd ) );
        return returnValue;
    }

    private boolean  checkAlphanumericOrBlank(String line, FieldCheckDto field, int positionStart, int positionEnd, String feedback) {

        boolean returnValue = false;

        if( !StringProcessor.isAlphaNumeric( line.substring( positionStart, positionEnd ) ) && !line.substring( positionEnd, positionEnd ).isBlank()) {
            field.setCorrect( false );
            field.setLine( Arrays.asList( this.lines ).indexOf( line ) +1 );
            field.setPositionStart( positionStart );
            field.setPositionEnd( positionEnd - 1 );
            field.addFeedback( feedback );
            returnValue = true;
        }
        field.setValue( line.substring( positionStart, positionEnd ) );

        return returnValue;
    }

    private boolean checkIsNumericOrBlank(String line, FieldCheckDto field, int positionStart, int positionEnd, String feedback) {

        boolean checkIsWrong = false;

        if( !StringProcessor.isNumeric( line.substring( positionStart, positionEnd ) ) && !line.substring( 244,246 ).isBlank() ) {
            field.setCorrect( false );
            field.setLine( Arrays.asList( this.lines ).indexOf( line ) +1 );
            field.setPositionStart( positionStart );
            field.setPositionEnd( positionEnd -1 );
            field.addFeedback( feedback );
            checkIsWrong = true;
        }
        field.setValue( line.substring( positionStart, positionEnd ) );

        return checkIsWrong;
    }

    private boolean checkAlphanumeric(String line, FieldCheckDto field, int positionStart, int positionEnd, String feedback) {

        boolean returnValue = false;

        if( !StringProcessor.isAlphaNumeric( line.substring( positionStart, positionEnd ) ) ) {
            field.setCorrect( false );
            field.setLine( Arrays.asList( this.lines ).indexOf( line ) + 1 );
            field.setPositionStart( positionStart );
            field.setPositionEnd( positionEnd - 1 );
            field.addFeedback( feedback );
            returnValue =true;

        }
        field.setValue( line.substring( positionStart, positionEnd ) );

        return returnValue;
    }

}
