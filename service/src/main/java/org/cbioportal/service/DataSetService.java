/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.service;

import java.util.List;
import org.cbioportal.model.DataSet;

/**
 *
 * @author jiaojiao
 */
public interface DataSetService {
    List<DataSet> getDataSets();
}
