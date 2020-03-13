package iterator;


import BigT.Map;
import heap.*;
import global.*;
import bufmgr.*;
import diskmgr.*;


import java.lang.*;
import java.io.*;

/**
 * open a heapfile and according to the condition expression to get
 * output file, call get_next to get all maps
 */
public class FileScanMap extends MapIterator {
    private Heapfile f;
    private Scan scan;
    private Map map1;
    private Map Jmap;
    private CondExpr[] OutputFilter;
    public FldSpec[] perm_mat;


    /**
     * constructor
     *
     * @param file_name  heapfile to be opened
     * @param n_out_flds number of fields in the out map
     * @param proj_list  shows what input fields go where in the output map
     * @param outFilter  select expressions
     * @throws IOException         some I/O fault
     * @throws FileScanException   exception from this class
     * @throws TupleUtilsException exception from this class
     * @throws InvalidRelation     invalid relation
     */
    public FileScanMap(String file_name,
                       FldSpec[] proj_list,
                       CondExpr[] outFilter
    )
            throws IOException,
            FileScanException,
            TupleUtilsException,
            InvalidRelation {

        OutputFilter = outFilter;
        perm_mat = proj_list;
        map1 = new Map();

        try {
            f = new Heapfile(file_name);

        } catch (Exception e) {
            throw new FileScanException(e, "Create new heapfile failed");
        }

        try {
            scan = f.openScanMap();
        } catch (Exception e) {
            throw new FileScanException(e, "openScan() failed");
        }
    }

    /**
     * @return shows what input fields go where in the output map
     */
    public FldSpec[] show() {
        return perm_mat;
    }

    /**
     * @return the result map
     * @throws JoinsException                 some join exception
     * @throws IOException                    I/O errors
     * @throws InvalidTupleSizeException      invalid tuple size
     * @throws InvalidTypeException           tuple type not valid
     * @throws PageNotReadException           exception from lower layer
     * @throws PredEvalException              exception from PredEval class
     * @throws UnknowAttrType                 attribute type unknown
     * @throws FieldNumberOutOfBoundException array out of bounds
     * @throws WrongPermat                    exception for wrong FldSpec argument
     */
    public Map get_next()
            throws JoinsException,
            IOException,
            InvalidTupleSizeException,
            InvalidTypeException,
            PageNotReadException,
            PredEvalException,
            UnknowAttrType,
            FieldNumberOutOfBoundException,
            WrongPermat {
        RID rid = new RID();
        ;

        while (true) {
            if ((map1 = scan.getNextMap(rid)) == null) {
                return null;
            }
            map1.setDefaultHdr();
            map1.setFldOffset(map1.getMapByteArray());
            if (PredEval.Eval(OutputFilter, map1, null, null, null) == true) {
                return map1;
            }
        }
    }

    /**
     * implement the abstract method close() from super class Iterator
     * to finish cleaning up
     */
    public void close() {

        if (!closeFlag) {
            scan.closescan();
            closeFlag = true;
        }
    }

}

