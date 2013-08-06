/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.referenceapplication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.referencemetadata.ReferenceMetadataProperties;
import org.openmrs.module.namephonetics.NamePhoneticsConstants;
import org.openmrs.module.registrationcore.RegistrationCoreConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class ReferenceApplicationActivator extends BaseModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing Reference Application Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Reference Application Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Reference Application Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
        AdministrationService administrationService = Context.getAdministrationService();
        AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);

        appFrameworkService.disableApp("registrationapp.basicRegisterPatient");

        administrationService.saveGlobalProperty(new GlobalProperty("registrationcore.patientNameSearch",
                "registrationcore.ExistingPatientNameSearch"));

        setupEmrApiGlobalProperties(administrationService);
        setupNamePhoneticsGlobalProperties(administrationService);
        setupRegistrationcoreGlobalProperties(administrationService);

        log.info("Reference Application Module started");
	}

    public void setupEmrApiGlobalProperties(AdministrationService administrationService) {
        setGlobalProperty(administrationService, EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, ReferenceMetadataProperties.ADMISSION_ENCOUNTER_TYPE_UUID);
        setGlobalProperty(administrationService, EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, ReferenceMetadataProperties.TRANSFER_ENCOUNTER_TYPE_UUID);
        setGlobalProperty(administrationService, EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, ReferenceMetadataProperties.DISCHARGE_ENCOUNTER_TYPE_UUID);
        setGlobalProperty(administrationService, EmrApiConstants.GP_CHECK_IN_ENCOUNTER_TYPE, ReferenceMetadataProperties.CHECK_IN_ENCOUNTER_TYPE_UUID);

        setGlobalProperty(administrationService, EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, ReferenceMetadataProperties.FACILITY_VISIT_TYPE_UUID);
    }

    private void setupNamePhoneticsGlobalProperties(AdministrationService administrationService) {
		setGlobalProperty(administrationService, NamePhoneticsConstants.GIVEN_NAME_GLOBAL_PROPERTY, "Soundex");
		setGlobalProperty(administrationService, NamePhoneticsConstants.MIDDLE_NAME_GLOBAL_PROPERTY, "Soundex");
		setGlobalProperty(administrationService, NamePhoneticsConstants.FAMILY_NAME_GLOBAL_PROPERTY, "Soundex");
		setGlobalProperty(administrationService, NamePhoneticsConstants.FAMILY_NAME2_GLOBAL_PROPERTY, "Soundex");
	}
	
	private void setupRegistrationcoreGlobalProperties(AdministrationService administrationService) {
		setGlobalProperty(administrationService, RegistrationCoreConstants.GP_PATIENT_NAME_SEARCH, "registrationcore.NamePhoneticsPatientNameSearch");
		setGlobalProperty(administrationService, RegistrationCoreConstants.GP_FAST_SIMILAR_PATIENT_SEARCH_ALGORITHM, "registrationcore.NamePhoneticsPatientSearchAlgorithm");
		setGlobalProperty(administrationService, RegistrationCoreConstants.GP_PRECISE_SIMILAR_PATIENT_SEARCH_ALGORITHM, "registrationcore.NamePhoneticsPatientSearchAlgorithm");
	}
	
    private void setGlobalProperty(AdministrationService administrationService, String propertyName, String propertyValue) {
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName, propertyValue);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

    /**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Reference Application Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Reference Application Module stopped");

        Context.getService(AppFrameworkService.class).enableApp("registrationapp.basicRegisterPatient");
	}
	
}
