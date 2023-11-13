/*
 * ===================== Original Benchmark stub License =====================
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.github.awidesky.jCipherUtilBenchmark;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.github.awidesky.jCipherUtil.AbstractCipherUtil;
import io.github.awidesky.jCipherUtil.cipher.symmetric.SymmetricCipherUtilBuilder;
import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AESKeySize;
import io.github.awidesky.jCipherUtil.cipher.symmetric.aes.AES_ECBCipherUtil;
import io.github.awidesky.jCipherUtil.messageInterface.InPut;

/*
 * mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jmh -DarchetypeArtifactId=jmh-java-benchmark-archetype -DgroupId=io.github.awidesky.jCipherUtilBenchmark -DartifactId=BenchmarkStub -Dversion=1.0
 *
 * 
 * Step 1: build Maven project
 *  1. Right click on project,
 *  2. Select Run As,
 *  3. Select Maven Build and specify goals as clean install
 * 
 * Step 2: run tests
 * 1. Right click on project,
 * 2. Select Run As,
 * 3. Select Java Application and choose either Main - org.openjdk.jmh or the main you created
 * 
 * */

@Warmup(iterations = 0)
@Measurement(iterations = 1)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class MyBenchmark {

    private static byte[] plain = new byte[4 * 1024 * 1024];
    private static byte[] encrypted;
    
    private static final SymmetricCipherUtilBuilder<?> b = new AES_ECBCipherUtil.Builder(AESKeySize.SIZE_128);
    private static InPut p;
    private static InPut e;
    private static Path pFile;
    private static Path eFile; 
    
    @Setup(Level.Trial)
    public static void genData() throws IOException {
    	new Random().nextBytes(plain);
    	encrypted = b.build("Hello, World!".toCharArray()).encryptToSingleBuffer(InPut.from(plain));
    	pFile = Files.createTempFile("plain", null);
    	eFile = Files.createTempFile("encrypt", null);
    	Files.write(pFile, plain);
    	Files.write(eFile, encrypted);
    	p = InPut.from(plain);
    	e = InPut.from(encrypted);
    }

	@Setup(Level.Invocation)
	public static void setInputs() throws FileNotFoundException {
		p = InPut.from(pFile.toFile());
		e = InPut.from(eFile.toFile());
	}
    
    @Benchmark
    public void engine_encrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = b.build("Hello, World!".toCharArray());
    	c.engine = true;
    	bh.consume(c.encryptToSingleBuffer(p));
    }
    @Benchmark
    public void engine_decrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = b.build("Hello, World!".toCharArray());
    	c.engine = true;
    	bh.consume(c.decryptToSingleBuffer(e));
    }
    
    
    @Benchmark
    public void original_encrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = b.build("Hello, World!".toCharArray());
    	c.engine = false;
    	bh.consume(c.encryptToSingleBuffer(p));
    }
    @Benchmark
    public void original_decrypt(Blackhole bh) throws Exception {
    	AbstractCipherUtil c = b.build("Hello, World!".toCharArray());
    	c.engine = false;
    	bh.consume(c.decryptToSingleBuffer(e));
    }
}


/*

M2 Mac:
Benchmark                                          Mode  Cnt     Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  2604.158          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  2648.704          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  2545.077          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  2585.176          ms/op


Benchmark                                          Mode  Cnt     Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  2621.669          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  2556.853          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  2631.344          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  2599.603          ms/op


7500F:
Benchmark                                          Mode  Cnt    Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  746.815          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  420.551          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  722.208          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  422.172          ms/op


Benchmark                                          Mode  Cnt    Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  750.734          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  427.570          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  712.423          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  431.535          ms/op



-------------------------------after init*() rewritten-------------------------------
Benchmark                                          Mode  Cnt    Score     Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    6  400.920 ±  24.617  ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    6  413.677 ±  15.995  ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    6  418.945 ± 136.495  ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    6  410.134 ±   9.251  ms/op


Benchmark                                          Mode  Cnt     Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2   979.069          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  1013.136          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2   914.981          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  1000.383          ms/op

Benchmark                                          Mode  Cnt     Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2   963.686          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  1007.763          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2   929.880          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  1006.198          ms/op



Benchmark                                          Mode  Cnt    Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  982.636          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  988.934          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  972.552          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  966.361          ms/op

Benchmark                                          Mode  Cnt     Score   Error  Units
jCipherUtilBenchmark.MyBenchmark.engine_decrypt    avgt    2  1785.134          ms/op
jCipherUtilBenchmark.MyBenchmark.engine_encrypt    avgt    2  1872.761          ms/op
jCipherUtilBenchmark.MyBenchmark.original_decrypt  avgt    2  1809.077          ms/op
jCipherUtilBenchmark.MyBenchmark.original_encrypt  avgt    2  1863.496          ms/op

     * it just isn't stable - the result bounce a lot. I'll just consider
     * the performance difference between engine version and original version
     * is trivial.
     *
 */