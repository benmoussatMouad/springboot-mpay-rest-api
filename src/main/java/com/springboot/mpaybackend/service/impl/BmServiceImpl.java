package com.springboot.mpaybackend.service.impl;

import com.springboot.mpaybackend.exception.MPayAPIException;
import com.springboot.mpaybackend.payload.BmFileCheckDto;
import com.springboot.mpaybackend.payload.FieldCheckDto;
import com.springboot.mpaybackend.repository.AgencyRepository;
import com.springboot.mpaybackend.repository.BankRepository;
import com.springboot.mpaybackend.service.BmService;
import com.springboot.mpaybackend.utils.StringProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;

@Service
public class BmServiceImpl implements BmService {

    BankRepository bankRepository;
    AgencyRepository agencyRepository;
    String[] lines;

    public BmServiceImpl(BankRepository bankRepository, AgencyRepository agencyRepository) {
        this.bankRepository = bankRepository;
        this.agencyRepository = agencyRepository;
    }

    @Override
    public BmFileCheckDto verifyFileContent(String content) {
        this.lines = content.split( System.lineSeparator() );
        if( lines.length < 3 ) {
            throw new MPayAPIException( HttpStatus.BAD_REQUEST, "The file should have 3 lines or more" );
        }
        BmFileCheckDto dto = null;
        dto = new BmFileCheckDto();
        dto.setAllCorrect( true );
        // Check first record 000
        dto = checkFirstRecord( lines[0], dto );

        // Check second record 001
        dto = checkSecondRecord( lines[1], dto );
        // Check last record 999

        dto = checkLastRecord( lines[2], dto );


        return dto;
    }

    private BmFileCheckDto checkLastRecord(String line, BmFileCheckDto dto) {
        if( checkIsNumeric( line, dto.getRecordType3(), 0, 3, "Last record type should be 999" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getNumberOfRecords(), 3, 10, "Number of records must numeric value in 7 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 10 ) != 'X' ) {
            dto.getLastRecordEndCharacter().setCorrect( false );
            dto.getLastRecordEndCharacter().setLine( 2 );
            dto.getLastRecordEndCharacter().setPositionStart( 9 );
            dto.getLastRecordEndCharacter().setPositionEnd( 9 );
            dto.getLastRecordEndCharacter().setFeedback( "End character must be X" );
            dto.getLastRecordEndCharacter().setValue( "" + line.charAt( 10 ) );
            dto.setAllCorrect( false );
        }
        return dto;
    }

    private BmFileCheckDto checkSecondRecord(String line, BmFileCheckDto dto) {

        if( checkIsNumeric( line, dto.getRecordType2(), 0, 3, "Record type should be numeric" ) ) {
            dto.setAllCorrect( false );
        }


        if( checkIsNumeric( line, dto.getMerchantSequentialNumber(), 3,10, "Sequential number should be numeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 10 ) != 'A' && line.charAt( 10 ) != 'C' && line.charAt( 10 ) != 'D' ) {
            dto.getMerchantUpdateCode().setCorrect( false );
            dto.getMerchantUpdateCode().setLine( 2 );
            dto.getMerchantUpdateCode().setPositionStart( 9 );
            dto.getMerchantUpdateCode().setPositionEnd( 9 );
            dto.getMerchantUpdateCode().addFeedback( "update code should be C or D or A" );
            dto.getMerchantUpdateCode().setValue( "" + line.charAt( 10 ) );
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getMerchantContractNumber(), 11, 26, "Contract number is not numeric" ) ) {
            dto.setAllCorrect( false );
        }

        String bankCode = line.substring( 11, 14 );
        String agencyCode = line.substring( 14, 19 );
        if( !bankRepository.existsByBankCode( bankCode ) ) {
            dto.getMerchantContractNumber().setCorrect( false );
            dto.getMerchantContractNumber().setLine( 2 );
            dto.getMerchantContractNumber().setPositionStart( 10 );
            dto.getMerchantContractNumber().setPositionEnd( 12 );
            dto.getMerchantContractNumber().addFeedback( "Bank code is not valid: Bank does not exist" );
            dto.setAllCorrect( false );
        }
        if( !agencyRepository.existsByAgencyCode( agencyCode ) ) {
            dto.getMerchantContractNumber().setCorrect( false );
            dto.getMerchantContractNumber().setLine( 2 );
            dto.getMerchantContractNumber().setPositionStart( 13 );
            dto.getMerchantContractNumber().setPositionEnd( 17 );
            dto.getMerchantContractNumber().addFeedback( "Agency code is not valid: agency with this code does not exist" );
            dto.setAllCorrect( false );
        }


        if( checkIsNumeric( line, dto.getMerchantIdDocumentType(), 26, 29, "Merchant Id document should be numeric value" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getMerchantNif(), 29, 44, "NIF should be numbers only" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getMerchantSocialReason(), 44, 74, "Social reason should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getTradeName(), 74, 104, "Trade name should be alpha numeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getMerchantExperienceYears(), 104, 107, "Experience years should be numeric or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getMerchantPrincipalContactName(), 107, 132, "Contract name should be alphanumeric" ) ) {
            dto.setAllCorrect( false );

        }

        if( checkIsNumeric(line, dto.getContractType(), 132, 135, "Contract type should be numeric"  ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getPrincipalContractTitle(), 135, 150, "Contract title should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericOrBlank( line, dto.getSecondContactName(), 150, 175, "Second contact name should be alphanumeric or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getRcNumber(), 175, 205, "RC should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getAddressLine1(), 205, 208, "Address ine 1 should be numeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine2(), 208, 245, "Address line 2 should be alphanumeric") ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getAddressLine3(), 245, 247, "Address line 3 should be numeric or blank" ) ) {
            dto.setAllCorrect( false );
        }


        if( checkAlphanumeric( line, dto.getAddressLine4(), 247, 267, "Address line 4 should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine5(), 267, 287, "Address line 5 should be alphanumeric" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumericOrBlank( line, dto.getAddressLine6(), 287,290, "Address line 6 (code wilaya) should be numeric in 3 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getAddressLine7(), 290, 305, "Adress line 7 (Libellé de la wilaya) should be alphanumeric in 15 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getAddressLine8(), 305, 310, "Address line 8 (Code postal de l’adresse) should be numeric value in 5 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getTradeCategory(), 310, 318, "Trade category should be numeric value in 8 positions : Selon codification ISO 8583 précédé par 0000" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getTradeLabel(), 318, 358, "Trade label should be alphanumeric in 40 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getMerchantPhoneNumber(), 358, 373, "Phone number must be alphanumeric in 15 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericOrBlank( line, dto.getMerchantFaxNumber(), 373, 388, "Fax number must be alphanumeric in 15 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericOrBlank( line, dto.getMerchantMobileNumber(), 388, 403, "Mobile phone number must be alphanumeric in 15 positions or blank" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericAndSpecialCharacters( line, dto.getMerchantEmailAddress(), 403, 453, "Merchant email address must be alphanumeric with special characters in 50 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric(line, dto.getMerchantRib(), 453, 473, "RIB should be alphanumeric in 20 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getMerchantAgencyCode(), 473, 478, "Code agency must be numeric value in 5 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumeric( line, dto.getMerchantAgencyLabel(), 478, 513, "Agency label must be alphanumeric in 35 positions" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkAlphanumericAndSpecialCharacters( line, dto.getWebsiteAddress(), 513, 553, "Website address must alphanumeric with special characters" ) ) {
            dto.setAllCorrect( false );
        }

        if( checkIsNumeric( line, dto.getMerchantThreshold(), 553, 565, "Merchant threshold should be numeric value" ) ) {
            dto.setAllCorrect( false );
        }

        if( line.charAt( 565 ) != 'X' ) {
            dto.getSecondRecordEndCharacter().setCorrect( false );
            dto.getSecondRecordEndCharacter().setLine( 2 );
            dto.getSecondRecordEndCharacter().setPositionStart( 564 );
            dto.getSecondRecordEndCharacter().setPositionEnd( 564 );
            dto.getSecondRecordEndCharacter().setFeedback( "End character must be X" );
            dto.getSecondRecordEndCharacter().setValue( "" + line.charAt( 565 ) );
            dto.setAllCorrect( false );
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

    private BmFileCheckDto checkFirstRecord(String line, BmFileCheckDto dto) {

        if( !line.startsWith( "000" ) ) {
            dto.getRecordType().setCorrect( false );
            dto.getRecordType().setPositionStart( 0 );
            dto.getRecordType().setPositionEnd( 2 );
            dto.getRecordType().setLine( 1 );
            dto.setAllCorrect( false );
        }
        if( !StringProcessor.isAlphaNumeric( line.substring( 3, 21 ) ) ) {
            dto.getFileName().setCorrect( false );
            dto.getFileName().setPositionStart( 3 );
            dto.getFileName().setPositionEnd( 20 );
            dto.getFileName().setLine( 1 );
            dto.setAllCorrect( false );

        }
        if( !StringProcessor.isNumeric( line.substring( 21, 27 ) ) ) {
            dto.getBankCode().setCorrect( false );
            dto.getBankCode().setPositionStart( 21 );
            dto.getBankCode().setPositionEnd( 26 );
            dto.getBankCode().setLine( 1 );
            dto.setAllCorrect( false );

        }
        if( line.charAt( 27 ) != 'X') {
            dto.getHeaderRecordEndCharacter().setCorrect( false );
            dto.getHeaderRecordEndCharacter().setPositionStart( 27 );
            dto.getHeaderRecordEndCharacter().setPositionEnd( 27 );
            dto.getHeaderRecordEndCharacter().setLine( 1 );
            dto.getHeaderRecordEndCharacter().addFeedback( "Header record should end with X" );
            dto.getHeaderRecordEndCharacter().setValue( "" +  line.charAt( 27 ) );
            dto.setAllCorrect( false );
        }

        return dto;
    }
}
