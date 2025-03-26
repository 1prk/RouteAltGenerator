package org.radsim.routing;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class ODCSVReader {
    public List<ODCsv> readCSV(String path) throws IOException {
        try (FileReader fileReader = new FileReader(path)) {
            CsvToBean<ODCsv> csvToBean = new CsvToBeanBuilder<ODCsv>(fileReader)
                    .withType(ODCsv.class)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        }
    }
}