package com.springboot.mpaybackend.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BmFileCheckDto {

    private boolean allCorrect;
    // Header Record
    private FieldCheckDto recordType = new FieldCheckDto();
    private FieldCheckDto fileName = new FieldCheckDto();
    private FieldCheckDto bankCode = new FieldCheckDto();
    private FieldCheckDto recordNumber = new FieldCheckDto();
    private FieldCheckDto headerRecordEndCharacter = new FieldCheckDto();
    //Second Record
    private FieldCheckDto recordType2 = new FieldCheckDto();
    private FieldCheckDto merchantSequentialNumber = new FieldCheckDto();
    private FieldCheckDto merchantUpdateCode = new FieldCheckDto();
    private FieldCheckDto merchantContractNumber = new FieldCheckDto();
    private FieldCheckDto merchantIdDocumentType = new FieldCheckDto();
    private FieldCheckDto merchantNif = new FieldCheckDto();
    private FieldCheckDto merchantSocialReason = new FieldCheckDto();
    private FieldCheckDto tradeName = new FieldCheckDto();
    private FieldCheckDto merchantExperienceYears = new FieldCheckDto();
    private FieldCheckDto merchantPrincipalContactName = new FieldCheckDto();
    private FieldCheckDto contractType = new FieldCheckDto();
    private FieldCheckDto principalContractTitle = new FieldCheckDto();
    private FieldCheckDto secondContractTitle = new FieldCheckDto();
    private FieldCheckDto secondContactName = new FieldCheckDto();
    private FieldCheckDto rcNumber = new FieldCheckDto();
    private FieldCheckDto addressLine1 = new FieldCheckDto();
    private FieldCheckDto addressLine2 = new FieldCheckDto();
    private FieldCheckDto addressLine3 = new FieldCheckDto();
    private FieldCheckDto addressLine4 = new FieldCheckDto();
    private FieldCheckDto addressLine5 = new FieldCheckDto();
    private FieldCheckDto addressLine6 = new FieldCheckDto();
    private FieldCheckDto addressLine7 = new FieldCheckDto();
    private FieldCheckDto addressLine8 = new FieldCheckDto();
    private FieldCheckDto tradeCategory = new FieldCheckDto();
    private FieldCheckDto tradeLabel = new FieldCheckDto();
    private FieldCheckDto merchantPhoneNumber = new FieldCheckDto();
    private FieldCheckDto merchantFaxNumber = new FieldCheckDto();
    private FieldCheckDto merchantMobileNumber = new FieldCheckDto();
    private FieldCheckDto merchantEmailAddress = new FieldCheckDto();
    private FieldCheckDto merchantRib = new FieldCheckDto();
    private FieldCheckDto merchantAgencyCode = new FieldCheckDto();
    private FieldCheckDto merchantAgencyLabel = new FieldCheckDto();
    private FieldCheckDto websiteAddress = new FieldCheckDto();
    private FieldCheckDto merchantThreshold = new FieldCheckDto();
    private FieldCheckDto secondRecordEndCharacter = new FieldCheckDto();

    // Last Record
    private FieldCheckDto recordType3 = new FieldCheckDto();
    private FieldCheckDto numberOfRecords = new FieldCheckDto();
    private FieldCheckDto lastRecordEndCharacter = new FieldCheckDto();
}
